package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends ApiException {
    protected PaymentFailedException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }

    public class paymentFailedException extends ApiException {
        public paymentFailedException() {
            super("PAYMENT_FAILED", "Payment processing failed", HttpStatus.BAD_REQUEST);
        }
    }
}

