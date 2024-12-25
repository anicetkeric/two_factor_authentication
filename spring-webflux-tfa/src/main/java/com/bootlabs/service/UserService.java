package com.bootlabs.service;

import com.bootlabs.model.dto.AuthenticationResponseDTO;
import com.bootlabs.model.dto.UserDTO;
import com.bootlabs.model.dto.UserRegisterDTO;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDTO> createUser(UserRegisterDTO userRegisterDTO);

    Mono<AuthenticationResponseDTO> generateUserToken(Authentication authentication);

    Mono<AuthenticationResponseDTO> generateUserTokenTfaAuthenticator(String code);

    Mono<UserDTO> getConnectedUser();

    Mono<String> initUserTotp();

    Mono<UserDTO> activationUserTotp(String totpCode);

    Mono<UserDTO> disableUserTotp();
}
