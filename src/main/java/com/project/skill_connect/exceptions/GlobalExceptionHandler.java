package com.project.skill_connect.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
        .getFieldErrors()
        .forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        return new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage());

    }

    @ExceptionHandler(AdminRegistrationNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAdminRegistration(
            AdminRegistrationNotAllowedException ex) {

        return new ApiError(HttpStatus.FORBIDDEN.value(), ex.getMessage());

    }
}
