package com.fleet.exception;

public class BusinessRuleException extends RuntimeException {
    /**
     * Creates an exception that signals a domain rule violation.
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}




