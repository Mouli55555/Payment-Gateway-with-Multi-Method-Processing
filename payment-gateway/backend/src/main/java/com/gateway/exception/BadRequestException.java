package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super("BAD_REQUEST_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
