package com.arnavgpt.valoride.exception;

import org.springframework.http.HttpStatus;

public class InvalidRideStateException extends BusinessException {

    public InvalidRideStateException(String currentState, String action) {
        super(
                String.format("Cannot %s ride in %s state", action, currentState),
                HttpStatus.BAD_REQUEST,
                "INVALID_RIDE_STATE"
        );
    }

    public InvalidRideStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_RIDE_STATE");
    }
}