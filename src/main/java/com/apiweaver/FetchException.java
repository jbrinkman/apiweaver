package com.apiweaver;

/**
 * Exception thrown when URL fetching operations fail.
 * This includes network errors, invalid URLs, timeouts, and HTTP errors.
 */
public class FetchException extends ApiWeaverException {
    
    public FetchException(String message) {
        super(message);
    }
    
    public FetchException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FetchException(String message, String context) {
        super(message, context);
    }
    
    public FetchException(String message, String context, Throwable cause) {
        super(message, context, cause);
    }
}