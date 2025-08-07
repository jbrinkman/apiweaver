package com.apiweaver;

/**
 * Base exception class for all ApiWeaver-specific exceptions.
 * Provides common functionality for error handling and context information.
 */
public class ApiWeaverException extends Exception {
    
    private final String context;
    
    public ApiWeaverException(String message) {
        super(message);
        this.context = null;
    }
    
    public ApiWeaverException(String message, Throwable cause) {
        super(message, cause);
        this.context = null;
    }
    
    public ApiWeaverException(String message, String context) {
        super(message);
        this.context = context;
    }
    
    public ApiWeaverException(String message, String context, Throwable cause) {
        super(message, cause);
        this.context = context;
    }
    
    /**
     * Gets the contextual information associated with this exception.
     * @return the context string, or null if no context was provided
     */
    public String getContext() {
        return context;
    }
    
    /**
     * Gets a detailed message including context information if available.
     * @return the detailed error message
     */
    public String getDetailedMessage() {
        if (context != null) {
            return getMessage() + " (Context: " + context + ")";
        }
        return getMessage();
    }
}