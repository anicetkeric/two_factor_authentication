package com.bootlabs.common.exception;

public class TOTPException extends RuntimeException {

    public TOTPException() {
    }

    public TOTPException(String message) {
        super(message);
    }

    public TOTPException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
