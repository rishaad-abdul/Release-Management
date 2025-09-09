package com.releasetracker.exception;

public class InvalidEnvironmentException extends RuntimeException {
    public InvalidEnvironmentException(String message) {
        super(message);
    }
}