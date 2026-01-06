package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class ExpiredCardException extends ApiException {
    public ExpiredCardException() {
        super("EXPIRED_CARD", "Card expired", HttpStatus.BAD_REQUEST);
    }
}
