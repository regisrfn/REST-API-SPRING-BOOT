package com.rufino.server.exception;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

public class ApiException {
    private String message = "Not OK";
    private String error;
    private Throwable throwable;
    private HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    public ApiException(String error, Throwable throwable, HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.setError(error);
        this.setThrowable(throwable);
        this.setHttpStatus(httpStatus);
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
