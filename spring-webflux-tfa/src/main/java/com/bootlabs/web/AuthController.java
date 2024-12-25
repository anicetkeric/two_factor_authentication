package com.bootlabs.web;

import com.bootlabs.common.exception.ValidatorException;
import com.bootlabs.model.dto.ApiResponseDTO;
import com.bootlabs.model.dto.LoginDTO;
import com.bootlabs.model.dto.UserRegisterDTO;
import com.bootlabs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/auth", produces = {"application/json"}, consumes = {"application/json"})
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    private final ReactiveAuthenticationManager authenticationManager;


    @Operation(operationId = "accountRegister",
            summary = "Create new app account with user",
            tags = {"Account"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "User account created.", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User account already exist", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Something went wrong", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = UserRegisterDTO.class)))
    )
    @PostMapping(value = {"/register"})
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponseDTO> accountRegister(@Parameter(name = "UserRegisterDTO", description = "", required = true, schema = @Schema(
            description = "")) @RequestBody @Valid UserRegisterDTO userRegisterDTO, @Parameter(hidden = true) ServerWebExchange exchange) {

        return Mono.just(userRegisterDTO)
                .map(userRegisterDTO1 -> {
                    boolean isPasswordMatches = StringUtils.hasText(StringUtils.trimAllWhitespace(userRegisterDTO1.password()))
                                                && StringUtils.hasText(StringUtils.trimAllWhitespace(userRegisterDTO1.passwordConfirmation()))
                                                && userRegisterDTO1.password().equals(userRegisterDTO1.passwordConfirmation());
                    if (!isPasswordMatches) {
                        throw new ValidatorException("Confirmation password does not match the new password");
                    }
                    return userRegisterDTO1;
                })
                .flatMap(userService::createUser)
                .map(savedUser -> new ApiResponseDTO(savedUser, "User created successfully"));
    }


    @Operation(operationId = "login",
            summary = "User login",
            tags = {"Account"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User account created.", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User account already exist", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Something went wrong", content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = LoginDTO.class)))
    )
    @PostMapping(value = {"/login"})
    public Mono<ApiResponseDTO> login(@Parameter(name = "LoginDTO", description = "", required = true, schema = @Schema(
            description = ""
    )) @RequestBody @Valid Mono<LoginDTO> loginDTO, @Parameter(hidden = true) ServerWebExchange exchange) {

        return loginDTO
                .flatMap(login ->
                        authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(login.email(), login.password()))
                                .flatMap(userService::generateUserToken)
                )
                .map(jwt -> new ApiResponseDTO(jwt, "User login successful"));
    }

}