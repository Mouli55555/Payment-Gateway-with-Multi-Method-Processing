package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class InvalidVpaException extends ApiException {
    public InvalidVpaException() {
        super("INVALID_VPA", "VPA format invalid", HttpStatus.BAD_REQUEST);
    }
}
