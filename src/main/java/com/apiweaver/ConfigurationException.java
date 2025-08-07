package com.apiweaver;

/**
 * Exception thrown when configuration validation or parsing fails.
 * This includes invalid command-line arguments, missing required parameters, and configuration conflicts.
 */
public class ConfigurationException extends ApiWeaverException {
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ConfigurationException(String message, String context) {
        super(message, context);
    }
    
    public ConfigurationException(String message, String context, Throwable cause) {
        super(message, context, cause);
    }
}