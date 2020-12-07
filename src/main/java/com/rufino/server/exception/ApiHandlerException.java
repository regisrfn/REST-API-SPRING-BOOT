package com.rufino.server.exception;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiHandlerException {

    @ExceptionHandler(value = { ApiRequestException.class })
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        HttpStatus httpStatus = e.getHttpStatus();
        Map<String, String> errors = new HashMap<>();
        errors.put("apiError", e.getMessage());
        ApiException apiException = new ApiException(errors, httpStatus, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDBException(DataIntegrityViolationException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = handleSqlError(e);
        ApiException apiException = new ApiException(errors, badRequest, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiException apiException = new ApiException(errors, badRequest, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, badRequest);
    }

    public Map<String, String> handleSqlError(DataIntegrityViolationException e) {
        String ss = e.getMessage();
        ss = ss.replace("\n", "").replace("\r", "");
        String pattern = ".*PreparedStatementCallback;.*SQL.*; ERROR:.*\"(\\w*user_\\w+)\".*";
        String error = (ss.replaceAll(pattern, "$1"));
        String[] errorString = error.split("_");
        Map<String, String> errors = new HashMap<>();

        if (errorString.length == 2) {
            // errorString = user_name
            error = "Value should not be empty";
            String fieldName = errorString[1].substring(0, 1).toUpperCase() + errorString[1].substring(1);
            errors.put(errorString[0] + fieldName, error);
        } else if ((errorString.length == 4)) {
            error = "Duplicated " + errorString[2];
            String fieldName = errorString[2].substring(0, 1).toUpperCase() + errorString[2].substring(1);
            errors.put(errorString[1] + fieldName, error);
        }

        return errors;
    }
}