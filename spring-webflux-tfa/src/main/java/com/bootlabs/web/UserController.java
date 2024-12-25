package com.bootlabs.web;

import com.bootlabs.model.dto.ApiResponseDTO;
import com.bootlabs.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/user", produces = {"application/json"}, consumes = {"application/json"})
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping(value = {"/me"})
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ApiResponseDTO> getConnectedUser(@Parameter(hidden = true) ServerWebExchange exchange) {
        return userService.getConnectedUser()
                .map(userDTO -> new ApiResponseDTO(userDTO, "User found"));
    }

    @PutMapping(value = {"/totp/generate"})
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ApiResponseDTO> setTOTPProcess(@Parameter(hidden = true) ServerWebExchange exchange) {
        return userService.initUserTotp()
                .map(userDTO -> new ApiResponseDTO(userDTO, "QRCODE generated"));
    }

    @PutMapping(value = {"/totp/enable"})
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ApiResponseDTO> activateTfa(@Parameter(name = "code",description = "The verification code",required = true,in = ParameterIn.QUERY) @RequestParam(value = "code",required = true) @NotNull @Valid String code, @Parameter(hidden = true) ServerWebExchange exchange) {
        return userService.activationUserTotp(code)
                .map(userDTO -> new ApiResponseDTO(userDTO, "TOTP activated"));
    }
    @PutMapping(value = {"/totp/disable"})
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ApiResponseDTO> disableTfa(@Parameter(hidden = true) ServerWebExchange exchange) {
        return userService.disableUserTotp()
                .map(userDTO -> new ApiResponseDTO(userDTO, "TOTP deactivated"));
    }

    @GetMapping(value = {"/totp/verify"})
    @PreAuthorize("hasAuthority('PRE_AUTH')")
    public Mono<ApiResponseDTO> checkAuthenticatorCode(@Parameter(name = "code",description = "The verification code",required = true,in = ParameterIn.QUERY) @RequestParam(value = "code",required = true) @NotNull @Valid String code, @Parameter(hidden = true) ServerWebExchange exchange) {
        return userService.generateUserTokenTfaAuthenticator(code)
                .map(userDTO -> new ApiResponseDTO(userDTO, "Successful authentication"));
    }

}
