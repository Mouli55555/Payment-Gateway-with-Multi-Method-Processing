package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super("NOT_FOUND_ERROR", message, HttpStatus.NOT_FOUND);
    }
}

