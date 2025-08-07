package com.apiweaver;

/**
 * Exception thrown when table extraction operations fail.
 * This includes malformed tables, missing columns, and parsing errors.
 */
public class ExtractionException extends ApiWeaverException {
    
    public ExtractionException(String message) {
        super(message);
    }
    
    public ExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ExtractionException(String message, String context) {
        super(message, context);
    }
    
    public ExtractionException(String message, String context, Throwable cause) {
        super(message, context, cause);
    }
}