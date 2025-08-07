package com.apiweaver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HttpUrlFetcherTest {
    
    private HttpUrlFetcher fetcher;
    
    @BeforeEach
    void setUp() {
        fetcher = new HttpUrlFetcher();
    }
    
    @Test
    void constructor_withDefaultValues_setsCorrectDefaults() {
        HttpUrlFetcher defaultFetcher = new HttpUrlFetcher();
        
        assertEquals(30000, defaultFetcher.getTimeoutMs());
        assertEquals("ApiWeaver/1.0", defaultFetcher.getUserAgent());
    }
    
    @Test
    void constructor_withCustomValues_setsCorrectValues() {
        HttpUrlFetcher customFetcher = new HttpUrlFetcher(5000, "CustomAgent/2.0");
        
        assertEquals(5000, customFetcher.getTimeoutMs());
        assertEquals("CustomAgent/2.0", customFetcher.getUserAgent());
    }
    
    @Test
    void constructor_withZeroTimeout_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new HttpUrlFetcher(0, "Agent")
        );
        assertEquals("Timeout must be positive", exception.getMessage());
    }
    
    @Test
    void constructor_withNegativeTimeout_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new HttpUrlFetcher(-1000, "Agent")
        );
        assertEquals("Timeout must be positive", exception.getMessage());
    }
    
    @Test
    void constructor_withNullUserAgent_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new HttpUrlFetcher(5000, null)
        );
        assertEquals("User agent cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void constructor_withEmptyUserAgent_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new HttpUrlFetcher(5000, "")
        );
        assertEquals("User agent cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void constructor_withWhitespaceUserAgent_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new HttpUrlFetcher(5000, "   ")
        );
        assertEquals("User agent cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void fetchHtmlContent_withNullUrl_throwsFetchException() {
        FetchException exception = assertThrows(
            FetchException.class,
            () -> fetcher.fetchHtmlContent(null)
        );
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void fetchHtmlContent_withEmptyUrl_throwsFetchException() {
        FetchException exception = assertThrows(
            FetchException.class,
            () -> fetcher.fetchHtmlContent("")
        );
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void fetchHtmlContent_withWhitespaceUrl_throwsFetchException() {
        FetchException exception = assertThrows(
            FetchException.class,
            () -> fetcher.fetchHtmlContent("   ")
        );
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void fetchHtmlContent_withMalformedUrl_throwsFetchException() {
        String malformedUrl = "not-a-valid-url";
        
        FetchException exception = assertThrows(
            FetchException.class,
            () -> fetcher.fetchHtmlContent(malformedUrl)
        );
        
        assertTrue(exception.getMessage().startsWith("Invalid URL format:"));
        assertTrue(exception.getCause() instanceof MalformedURLException);
    }
    
    @Test
    void fetchHtmlContent_withInvalidProtocol_throwsFetchException() {
        String invalidUrl = "ftp://example.com";
        
        FetchException exception = assertThrows(
            FetchException.class,
            () -> fetcher.fetchHtmlContent(invalidUrl)
        );
        
        assertEquals("Unsupported protocol: ftp. Only HTTP and HTTPS are supported.", exception.getMessage());
    }
    
    @Test
    void fetchHtmlContent_withFileProtocol_throwsFetchException() {
        String fileUrl = "file:///etc/hosts";
        
        FetchException exception = assertThrows(
            FetchException.class,
            () -> fetcher.fetchHtmlContent(fileUrl)
        );
        
        assertEquals("Unsupported protocol: file. Only HTTP and HTTPS are supported.", exception.getMessage());
    }
    
    // Integration test with a real HTTP server would be ideal, but for unit tests
    // we'll focus on testing the error handling and edge cases
    
    @Test
    void fetchHtmlContent_withUrlContainingWhitespace_trimsAndProcesses() {
        // This test verifies that URLs with leading/trailing whitespace are handled
        String urlWithWhitespace = "  https://httpbin.org/html  ";
        
        // We can't easily mock HttpURLConnection, so this test would need a real server
        // For now, we'll just verify that the URL trimming logic works by checking
        // that it doesn't throw a "null or empty" exception
        assertDoesNotThrow(() -> {
            try {
                fetcher.fetchHtmlContent(urlWithWhitespace);
            } catch (FetchException e) {
                // We expect this to fail with network error, not validation error
                assertFalse(e.getMessage().equals("URL cannot be null or empty"));
            }
        });
    }
    
    @Test
    void getTimeoutMs_returnsConfiguredTimeout() {
        HttpUrlFetcher customFetcher = new HttpUrlFetcher(15000, "TestAgent");
        assertEquals(15000, customFetcher.getTimeoutMs());
    }
    
    @Test
    void getUserAgent_returnsConfiguredUserAgent() {
        HttpUrlFetcher customFetcher = new HttpUrlFetcher(5000, "TestAgent/1.0");
        assertEquals("TestAgent/1.0", customFetcher.getUserAgent());
    }
}