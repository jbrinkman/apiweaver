package com.apiweaver;

/**
 * Exception thrown when table extraction operations fail.
 * This includes malformed tables, missing columns, and parsing errors.
 */
public class ExtractionException extends Exception {
    
    public ExtractionException(String message) {
        super(message);
    }
    
    public ExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}