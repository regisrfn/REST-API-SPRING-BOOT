package com.rufino.server.exception;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.rufino.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiHandlerException {

    @Autowired
    private UserService userService;

    @ExceptionHandler(value = { ApiRequestException.class })
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, String> errors = new HashMap<>();
        errors.put("apiError", e.getMessage());
        ApiException apiException = new ApiException(errors, httpStatus, ZonedDateTime.now());

        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDBException(DataIntegrityViolationException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = userService.handleSqlError(e);
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
}