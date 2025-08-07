package com.apiweaver;

/**
 * Exception thrown when HTML parsing operations fail.
 * This includes malformed HTML, missing elements, and DOM parsing errors.
 */
public class ParseException extends ApiWeaverException {
    
    public ParseException(String message) {
        super(message);
    }
    
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ParseException(String message, String context) {
        super(message, context);
    }
    
    public ParseException(String message, String context, Throwable cause) {
        super(message, context, cause);
    }
}