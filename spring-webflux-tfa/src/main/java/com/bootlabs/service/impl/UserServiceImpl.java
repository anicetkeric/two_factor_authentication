package com.bootlabs.service.impl;

import com.bootlabs.common.exception.EmailAlreadyUsedException;
import com.bootlabs.common.exception.FailedAuthenticationException;
import com.bootlabs.common.exception.TOTPException;
import com.bootlabs.common.exception.ValidatorException;
import com.bootlabs.document.User;
import com.bootlabs.document.UserTfa;
import com.bootlabs.model.dto.AuthenticationResponseDTO;
import com.bootlabs.model.dto.UserDTO;
import com.bootlabs.model.dto.UserRegisterDTO;
import com.bootlabs.model.enums.RoleEnum;
import com.bootlabs.repository.UserRepository;
import com.bootlabs.security.SecurityUtils;
import com.bootlabs.security.TokenProvider;
import com.bootlabs.mapper.UserMapper;
import com.bootlabs.service.UserService;
import com.bootlabs.utils.qr.QrCodeGeneratorUtils;
import com.bootlabs.utils.totp.TOTPUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

/**
 * Service class for managing users.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;
    private final UserMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDTO> createUser(UserRegisterDTO userRegisterDTO) {
        return userRepository.findOneByUsernameIgnoreCase(userRegisterDTO.email())
                .flatMap(existingUser -> Mono.error(new EmailAlreadyUsedException()))
                .cast(User.class)

                .switchIfEmpty(Mono.defer(() -> {

                            String encryptedPassword = passwordEncoder.encode(userRegisterDTO.password());

                            var newUser = User.builder()
                                    .fullName(userRegisterDTO.fullname())
                                    .username(userRegisterDTO.email())
                                    .password(encryptedPassword)
                                    .roles(Collections.singleton(RoleEnum.USER))
                                    .enabled(true)
                                    .accountNonLocked(false)
                                    .accountNonExpired(false)
                                    .credentialsNonExpired(false)
                                    .build();

                            return userRepository.save(newUser);
                        })
                )
                .map(mapper::toDto);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AuthenticationResponseDTO> generateUserToken(Authentication authentication) {
        LOGGER.info("Generate user token for: {}", authentication.getName());
        String userEmail = StringUtils.trimToNull(authentication.getName().toLowerCase());
        return userRepository.findOneByUsernameIgnoreCase(userEmail)
                .switchIfEmpty(Mono.error(new FailedAuthenticationException("Authentication process failed.")))
                .map(user -> new AuthenticationResponseDTO(
                        tokenProvider.generateToken(authentication, (Objects.nonNull(user.getMfa()) && user.getMfa().isStatus())),
                        null,
                        (Objects.nonNull(user.getMfa()) && user.getMfa().isStatus())
                ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AuthenticationResponseDTO> generateUserTokenTfaAuthenticator(String code) {
        return getCurrentUser()
                .zipWhen(user -> SecurityUtils.getCurrentUserAuthentication())
                .map(user -> {
                    if (Objects.nonNull(user.getT1().getMfa()) && user.getT1().getMfa().isStatus()
                        && TOTPUtils.getTOTPCode(user.getT1().getMfa().getActivationSecretKey()).equals(code)) {
                        return new AuthenticationResponseDTO(
                                tokenProvider.generateToken(user.getT2(), false),
                                null,
                                true
                        );
                    }
                    throw new TOTPException("Unauthorized");
                });
    }

    protected Mono<User> getCurrentUser() {
        return SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByUsernameIgnoreCase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDTO> getConnectedUser() {
        return getCurrentUser()
                .map(mapper::toDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<String> initUserTotp() {
        return getCurrentUser()
                .map(user -> {
                    if (!user.isEnabled()) {
                        throw new ValidatorException("Inactive user");
                    }
                    if ((Objects.nonNull(user.getMfa()) && user.getMfa().isStatus())) {
                        throw new ValidatorException("You have already enabled two-factor authentication");
                    }
                    return user;
                })
                .flatMap(user -> {
                    user.setMfa(new UserTfa());
                    var secretKey = TOTPUtils.generateSecretKey();
                    user.getMfa().setActivationSecretKey(secretKey);
                    return userRepository.save(user)
                            .map(mapper::toDto)
                            .map(userDTO -> getAuthenticatorQrCodeBase64(userDTO, secretKey));
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDTO> disableUserTotp() {
        return getCurrentUser()
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        throw new ValidatorException("Inactive user");
                    }
                    user.setMfa(new UserTfa());
                    return userRepository.save(user);
                })
                .map(mapper::toDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDTO> activationUserTotp(String totpCode) {
        return getCurrentUser()
                .map(user -> {
                    if (!user.isEnabled()) {
                        throw new ValidatorException("Inactive user");
                    }
                    if ((Objects.nonNull(user.getMfa()) && user.getMfa().isStatus())) {
                        throw new ValidatorException("You have already enabled two-factor authentication");
                    }
                    if (TOTPUtils.getTOTPCode(user.getMfa().getActivationSecretKey()).equals(totpCode)) {
                        user.getMfa().setStatus(true);
                        user.getMfa().setActivationDate(LocalDateTime.now());

                        return user;
                    }
                    throw new ValidatorException("Two-factor authentication activation failed, Please try again");
                })
                .flatMap(user -> userRepository.save(user)
                        .map(mapper::toDto));
    }

    private String getAuthenticatorQrCodeBase64(UserDTO userDTO, String secretKey) {
        var account = MessageFormat.format("{0}@{1}", userDTO.getUsername(), "bootlabs");
        var authenticatorUrl = TOTPUtils.getGoogleAuthenticatorBarCode(secretKey, account, "Spring Weblux TOTP");
        return QrCodeGeneratorUtils.generateQRCode(authenticatorUrl);
    }
}
