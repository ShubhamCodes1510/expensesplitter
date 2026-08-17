package com.expense.expensesplitter.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor for single parameter
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor for resource and field (String, String)
    public ResourceNotFoundException(String resource, String field) {
        super(resource + " not found with " + field);
    }

    // Constructor for resource and id (String, Long)
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
}