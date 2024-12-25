package com.bootlabs.common.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.*;

import java.util.*;

import static com.bootlabs.common.AppConstant.CODE_KEY;
import static com.bootlabs.common.AppConstant.MESSAGE_KEY;

record ExceptionRule(Class<?> exceptionClass, HttpStatus status){}

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    // List of custom error
    private final List<ExceptionRule> exceptionsRules = List.of(
            new ExceptionRule(TOTPException.class, HttpStatus.UNAUTHORIZED),
            new ExceptionRule(ExpiredJwtException.class, HttpStatus.UNAUTHORIZED),
            new ExceptionRule(io.jsonwebtoken.security.SignatureException.class, HttpStatus.UNAUTHORIZED),
            new ExceptionRule(MalformedJwtException.class, HttpStatus.UNAUTHORIZED),
            new ExceptionRule(UnsupportedJwtException.class, HttpStatus.UNAUTHORIZED),
            new ExceptionRule(BadCredentialsException.class, HttpStatus.UNAUTHORIZED),
            new ExceptionRule(ValidatorException.class, HttpStatus.BAD_REQUEST),
            new ExceptionRule(EmailAlreadyUsedException.class, HttpStatus.NOT_FOUND),
            new ExceptionRule(UsernameAlreadyUsedException.class, HttpStatus.NOT_FOUND)
    );

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {

        Throwable error = getError(request);

        Optional<ExceptionRule> exceptionRuleOptional = exceptionsRules.stream()
                .map(exceptionRule -> exceptionRule.exceptionClass().isInstance(error) ? exceptionRule : null)
                .filter(Objects::nonNull)
                .findFirst();

       return exceptionRuleOptional.<Map<String, Object>>map(exceptionRule -> Map.of(CODE_KEY, exceptionRule.status().value(), MESSAGE_KEY, error.getMessage()))
               .orElseGet(() -> Map.of(CODE_KEY, determineHttpStatus(error).value(),  MESSAGE_KEY, error.getMessage()));
    }

    private HttpStatusCode determineHttpStatus(Throwable error) {
        return error instanceof ResponseStatusException err ? err.getStatusCode() : MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class).getValue(CODE_KEY, HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}