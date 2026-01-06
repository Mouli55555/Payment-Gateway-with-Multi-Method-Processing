package com.gateway.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public ApiException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public static ApiException badRequest(String message) {
        return new ApiException("BAD_REQUEST_ERROR", message, HttpStatus.BAD_REQUEST);
    }

    // ✅ ADD THIS
    public static ApiException badRequest(String code, String message) {
        return new ApiException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static ApiException notFound(String message) {
        return new ApiException("NOT_FOUND_ERROR", message, HttpStatus.NOT_FOUND);
    }

    public static ApiException authError() {
        return new ApiException(
                "AUTHENTICATION_ERROR",
                "Invalid API credentials",
                HttpStatus.UNAUTHORIZED
        );
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
