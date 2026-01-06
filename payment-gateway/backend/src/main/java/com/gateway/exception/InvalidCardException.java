package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class InvalidCardException extends ApiException {
    public InvalidCardException(String message) {
        super("INVALID_CARD", message, HttpStatus.BAD_REQUEST);
    }
}
