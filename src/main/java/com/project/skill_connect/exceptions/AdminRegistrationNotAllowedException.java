package com.project.skill_connect.exceptions;

public class AdminRegistrationNotAllowedException extends RuntimeException {
    public AdminRegistrationNotAllowedException(String message) {
        super(message);
    }
}
