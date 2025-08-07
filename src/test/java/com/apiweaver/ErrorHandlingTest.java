package com.apiweaver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Comprehensive test suite for error handling and exception scenarios
 * across all ApiWeaver components.
 */
class ErrorHandlingTest {

    @Mock
    private UrlFetcher mockUrlFetcher;
    
    @Mock
    private HtmlParser mockHtmlParser;
    
    @Mock
    private TableExtractor mockTableExtractor;
    
    @Mock
    private PropertyMapper mockPropertyMapper;
    
    @Mock
    private OpenApiGenerator mockOpenApiGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("HttpUrlFetcher should handle null URL gracefully")
    void testHttpUrlFetcher_NullUrl() {
        HttpUrlFetcher fetcher = new HttpUrlFetcher();
        
        FetchException exception = assertThrows(FetchException.class, () -> {
            fetcher.fetchHtmlContent(null);
        });
        
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("HttpUrlFetcher should handle empty URL gracefully")
    void testHttpUrlFetcher_EmptyUrl() {
        HttpUrlFetcher fetcher = new HttpUrlFetcher();
        
        FetchException exception = assertThrows(FetchException.class, () -> {
            fetcher.fetchHtmlContent("   ");
        });
        
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("HttpUrlFetcher should handle invalid URL format")
    void testHttpUrlFetcher_InvalidUrlFormat() {
        HttpUrlFetcher fetcher = new HttpUrlFetcher();
        
        FetchException exception = assertThrows(FetchException.class, () -> {
            fetcher.fetchHtmlContent("not-a-valid-url");
        });
        
        assertTrue(exception.getMessage().contains("Invalid URL format"));
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("HttpUrlFetcher should handle unsupported protocol")
    void testHttpUrlFetcher_UnsupportedProtocol() {
        HttpUrlFetcher fetcher = new HttpUrlFetcher();
        
        FetchException exception = assertThrows(FetchException.class, () -> {
            fetcher.fetchHtmlContent("ftp://example.com");
        });
        
        assertTrue(exception.getMessage().contains("Unsupported protocol: ftp"));
    }

    @Test
    @DisplayName("HttpUrlFetcher constructor should validate timeout")
    void testHttpUrlFetcher_InvalidTimeout() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new HttpUrlFetcher(-1, "TestAgent");
        });
        
        assertEquals("Timeout must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("HttpUrlFetcher constructor should validate user agent")
    void testHttpUrlFetcher_InvalidUserAgent() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new HttpUrlFetcher(5000, null);
        });
        
        assertEquals("User agent cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("JSoupHtmlParser should handle null HTML content")
    void testJSoupHtmlParser_NullHtmlContent() {
        JSoupHtmlParser parser = new JSoupHtmlParser();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseHtml(null);
        });
        
        assertEquals("HTML content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("JSoupHtmlParser should handle empty HTML content")
    void testJSoupHtmlParser_EmptyHtmlContent() {
        JSoupHtmlParser parser = new JSoupHtmlParser();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseHtml("");
        });
        
        assertEquals("HTML content cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("JSoupHtmlParser should handle null document in findH2ElementsWithIdEndingIn")
    void testJSoupHtmlParser_NullDocumentInFindH2() {
        JSoupHtmlParser parser = new JSoupHtmlParser();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.findH2ElementsWithIdEndingIn(null, "ObjectValues");
        });
        
        assertEquals("Document cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("JSoupHtmlParser should handle null suffix in findH2ElementsWithIdEndingIn")
    void testJSoupHtmlParser_NullSuffixInFindH2() {
        JSoupHtmlParser parser = new JSoupHtmlParser();
        Document doc = parser.parseHtml("<html><body></body></html>");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.findH2ElementsWithIdEndingIn(doc, null);
        });
        
        assertEquals("Suffix cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("PropertyTableExtractor should handle null table")
    void testPropertyTableExtractor_NullTable() {
        PropertyTableExtractor extractor = new PropertyTableExtractor();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(null);
        });
        
        assertEquals("Table element cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("PropertyTableExtractor should handle non-table element")
    void testPropertyTableExtractor_NonTableElement() {
        PropertyTableExtractor extractor = new PropertyTableExtractor();
        JSoupHtmlParser parser = new JSoupHtmlParser();
        Document doc = parser.parseHtml("<html><body><div>Not a table</div></body></html>");
        Element divElement = doc.select("div").first();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(divElement);
        });
        
        assertTrue(exception.getMessage().contains("Element is not a table"));
    }

    @Test
    @DisplayName("PropertyTableExtractor should handle empty table")
    void testPropertyTableExtractor_EmptyTable() {
        PropertyTableExtractor extractor = new PropertyTableExtractor();
        JSoupHtmlParser parser = new JSoupHtmlParser();
        Document doc = parser.parseHtml("<html><body><table></table></body></html>");
        Element tableElement = doc.select("table").first();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(tableElement);
        });
        
        assertEquals("Table contains no rows", exception.getMessage());
    }

    @Test
    @DisplayName("TimeTapPropertyMapper should handle null property definition")
    void testTimeTapPropertyMapper_NullProperty() {
        TimeTapPropertyMapper mapper = new TimeTapPropertyMapper();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mapper.mapToOpenApiProperty(null);
        });
        
        assertEquals("Property definition cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("TimeTapPropertyMapper should handle invalid property definition")
    void testTimeTapPropertyMapper_InvalidProperty() {
        TimeTapPropertyMapper mapper = new TimeTapPropertyMapper();
        PropertyDefinition invalidProperty = new PropertyDefinition("", "string", false, true, "");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mapper.mapToOpenApiProperty(invalidProperty);
        });
        
        assertTrue(exception.getMessage().contains("Property definition is not valid"));
    }

    @Test
    @DisplayName("ApiWeaverException should provide context information")
    void testApiWeaverException_ContextInformation() {
        String message = "Test error message";
        String context = "Test context";
        Throwable cause = new RuntimeException("Root cause");
        
        ApiWeaverException exception = new ApiWeaverException(message, context, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(context, exception.getContext());
        assertEquals(cause, exception.getCause());
        assertEquals(message + " (Context: " + context + ")", exception.getDetailedMessage());
    }

    @Test
    @DisplayName("ApiWeaverException should handle null context")
    void testApiWeaverException_NullContext() {
        String message = "Test error message";
        
        ApiWeaverException exception = new ApiWeaverException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getContext());
        assertEquals(message, exception.getDetailedMessage());
    }

    @Test
    @DisplayName("FetchException should inherit from ApiWeaverException")
    void testFetchException_Inheritance() {
        FetchException exception = new FetchException("Test message");
        
        assertTrue(exception instanceof ApiWeaverException);
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    @DisplayName("ParseException should inherit from ApiWeaverException")
    void testParseException_Inheritance() {
        ParseException exception = new ParseException("Test message");
        
        assertTrue(exception instanceof ApiWeaverException);
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    @DisplayName("ExtractionException should inherit from ApiWeaverException")
    void testExtractionException_Inheritance() {
        ExtractionException exception = new ExtractionException("Test message");
        
        assertTrue(exception instanceof ApiWeaverException);
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    @DisplayName("GenerationException should inherit from ApiWeaverException")
    void testGenerationException_Inheritance() {
        GenerationException exception = new GenerationException("Test message");
        
        assertTrue(exception instanceof ApiWeaverException);
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    @DisplayName("ConfigurationException should inherit from ApiWeaverException")
    void testConfigurationException_Inheritance() {
        ConfigurationException exception = new ConfigurationException("Test message");
        
        assertTrue(exception instanceof ApiWeaverException);
        assertEquals("Test message", exception.getMessage());
    }

    @Test
    @DisplayName("Exception hierarchy should support context and cause")
    void testExceptionHierarchy_ContextAndCause() {
        String message = "Test message";
        String context = "Test context";
        Throwable cause = new IOException("IO error");
        
        FetchException fetchException = new FetchException(message, context, cause);
        ParseException parseException = new ParseException(message, context, cause);
        ExtractionException extractionException = new ExtractionException(message, context, cause);
        GenerationException generationException = new GenerationException(message, context, cause);
        ConfigurationException configurationException = new ConfigurationException(message, context, cause);
        
        // Test all exception types have proper context and cause
        assertEquals(context, fetchException.getContext());
        assertEquals(cause, fetchException.getCause());
        assertEquals(context, parseException.getContext());
        assertEquals(cause, parseException.getCause());
        assertEquals(context, extractionException.getContext());
        assertEquals(cause, extractionException.getCause());
        assertEquals(context, generationException.getContext());
        assertEquals(cause, generationException.getCause());
        assertEquals(context, configurationException.getContext());
        assertEquals(cause, configurationException.getCause());
    }
}
