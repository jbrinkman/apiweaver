package com.apiweaver;

/**
 * Exception thrown when OpenAPI specification generation fails.
 * This includes schema creation errors, file I/O errors, and YAML/JSON processing errors.
 */
public class GenerationException extends ApiWeaverException {
    
    public GenerationException(String message) {
        super(message);
    }
    
    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GenerationException(String message, String context) {
        super(message, context);
    }
    
    public GenerationException(String message, String context, Throwable cause) {
        super(message, context, cause);
    }
}