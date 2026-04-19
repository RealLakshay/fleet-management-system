package com.fleet.exception;

public class ResourceNotFoundException extends RuntimeException {
    /**
     * Creates an exception for a missing resource identified by a field value.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + " = " + fieldValue);
    }
}




