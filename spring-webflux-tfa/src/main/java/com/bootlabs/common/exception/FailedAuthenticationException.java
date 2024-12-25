package com.bootlabs.common.exception;

public class FailedAuthenticationException extends RuntimeException {

    public FailedAuthenticationException() {
    }

    public FailedAuthenticationException(String message) {
        super(message);
    }

    public FailedAuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}