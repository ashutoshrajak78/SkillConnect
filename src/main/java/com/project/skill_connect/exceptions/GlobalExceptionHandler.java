package com.project.skill_connect.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

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

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return new ApiError(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage()
        );
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleResourceNotFound(
            ResourceNotFoundException ex) {

        return new ApiError(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage()
        );
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {

        return new ApiError(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicateApplication(
            DuplicateApplicationException ex) {

        return new ApiError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolation(
            ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Invalid request parameters");

        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                message
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(
            ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        ApiError error = new ApiError(
                status.value(),
                ex.getReason() != null ? ex.getReason() : "Request failed"
        );

        return ResponseEntity.status(status).body(error);
    }

}
