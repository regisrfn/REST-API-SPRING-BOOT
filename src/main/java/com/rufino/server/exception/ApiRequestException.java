package com.rufino.server.exception;

public class ApiRequestException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
