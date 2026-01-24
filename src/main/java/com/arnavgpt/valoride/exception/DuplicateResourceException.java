package com.arnavgpt.valoride.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resource, String field, Object value) {
        super(
                String.format("%s already exists with %s: %s", resource, field, value),
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE"
        );
    }

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}