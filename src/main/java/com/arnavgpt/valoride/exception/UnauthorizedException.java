package com.arnavgpt.valoride.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public UnauthorizedException() {
        super("Authentication required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}