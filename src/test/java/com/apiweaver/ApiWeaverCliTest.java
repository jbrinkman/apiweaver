package com.apiweaver;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiWeaverCli class.
 */
class ApiWeaverCliTest {
    
    private ApiWeaverCli cli;
    
    @BeforeEach
    void setUp() {
        cli = new ApiWeaverCli();
    }
    
    @Test
    void testParseArguments_WithMinimalValidArgs() throws ParseException {
        String[] args = {"https://example.com/api-docs"};
        
        Configuration config = cli.parseArguments(args);
        
        assertNotNull(config);
        assertEquals("https://example.com/api-docs", config.getUrl());
        assertEquals("generated-api.yaml", config.getOutputFile());
        assertFalse(config.isVerbose());
        assertNull(config.getExistingSpecFile());
        assertEquals(30000, config.getTimeoutMs());
    }
    
    @Test
    void testParseArguments_WithAllOptions() throws ParseException {
        String[] args = {
            "-o", "custom-output.yaml",
            "-e", "existing-spec.yaml", 
            "-v",
            "-t", "60000",
            "https://example.com/api-docs"
        };
        
        Configuration config = cli.parseArguments(args);
        
        assertNotNull(config);
        assertEquals("https://example.com/api-docs", config.getUrl());
        assertEquals("custom-output.yaml", config.getOutputFile());
        assertEquals("existing-spec.yaml", config.getExistingSpecFile());
        assertTrue(config.isVerbose());
        assertEquals(60000, config.getTimeoutMs());
    }
    
    @Test
    void testParseArguments_WithLongOptions() throws ParseException {
        String[] args = {
            "--output", "long-output.yaml",
            "--existing", "long-existing.yaml",
            "--verbose",
            "--timeout", "45000",
            "https://example.com/api-docs"
        };
        
        Configuration config = cli.parseArguments(args);
        
        assertNotNull(config);
        assertEquals("https://example.com/api-docs", config.getUrl());
        assertEquals("long-output.yaml", config.getOutputFile());
        assertEquals("long-existing.yaml", config.getExistingSpecFile());
        assertTrue(config.isVerbose());
        assertEquals(45000, config.getTimeoutMs());
    }
    
    @Test
    void testParseArguments_WithHelpOption() throws ParseException {
        String[] args = {"-h"};
        
        Configuration config = cli.parseArguments(args);
        
        assertNull(config); // Help should return null
    }
    
    @Test
    void testParseArguments_WithHelpLongOption() throws ParseException {
        String[] args = {"--help"};
        
        Configuration config = cli.parseArguments(args);
        
        assertNull(config); // Help should return null
    }
    
    @Test
    void testParseArguments_NoUrlProvided() {
        String[] args = {"-o", "output.yaml"};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Exactly one URL argument is required"));
    }
    
    @Test
    void testParseArguments_MultipleUrlsProvided() {
        String[] args = {"https://example1.com", "https://example2.com"};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Exactly one URL argument is required"));
    }
    
    @Test
    void testParseArguments_InvalidUrl() {
        String[] args = {"invalid-url"};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("URL must start with http:// or https://"));
    }
    
    @Test
    void testParseArguments_InvalidTimeout() {
        String[] args = {"-t", "invalid", "https://example.com"};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Invalid timeout value"));
    }
    
    @Test
    void testParseArguments_NegativeTimeout() {
        String[] args = {"-t", "-1000", "https://example.com"};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Timeout must be a positive integer"));
    }
    
    @Test
    void testParseArguments_ZeroTimeout() {
        String[] args = {"-t", "0", "https://example.com"};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Timeout must be a positive integer"));
    }
    
    @Test
    void testParseArguments_HttpUrl() throws ParseException {
        String[] args = {"http://example.com/api-docs"};
        
        Configuration config = cli.parseArguments(args);
        
        assertNotNull(config);
        assertEquals("http://example.com/api-docs", config.getUrl());
    }
    
    @Test
    void testParseArguments_HttpsUrl() throws ParseException {
        String[] args = {"https://example.com/api-docs"};
        
        Configuration config = cli.parseArguments(args);
        
        assertNotNull(config);
        assertEquals("https://example.com/api-docs", config.getUrl());
    }
    
    @Test
    void testParseArguments_UnknownOption() {
        String[] args = {"--unknown-option", "https://example.com"};
        
        assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
    }
    
    @Test
    void testParseArguments_MissingOptionValue() {
        String[] args = {"-o", "https://example.com"};
        
        // This should work as -o takes the URL as its value, but then no URL is left
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Exactly one URL argument is required"));
    }
    
    @Test
    void testParseArguments_EmptyArgs() {
        String[] args = {};
        
        ParseException exception = assertThrows(ParseException.class, () -> {
            cli.parseArguments(args);
        });
        
        assertTrue(exception.getMessage().contains("Exactly one URL argument is required"));
    }
    
    @Test
    void testParseArguments_ValidConfiguration() throws ParseException {
        String[] args = {
            "-o", "test-output.yaml",
            "-e", "test-existing.yaml",
            "-v",
            "-t", "15000",
            "https://api.example.com/docs"
        };
        
        Configuration config = cli.parseArguments(args);
        
        assertNotNull(config);
        assertTrue(config.isValid());
        assertEquals("https://api.example.com/docs", config.getUrl());
        assertEquals("test-output.yaml", config.getOutputFile());
        assertEquals("test-existing.yaml", config.getExistingSpecFile());
        assertTrue(config.isVerbose());
        assertEquals(15000, config.getTimeoutMs());
    }
}