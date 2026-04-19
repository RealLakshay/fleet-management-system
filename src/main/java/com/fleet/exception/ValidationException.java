package com.fleet.exception;

public class ValidationException extends RuntimeException {
    /**
     * Creates an exception that represents validation failure.
     */
    public ValidationException(String message) {
        super(message);
    }
}




