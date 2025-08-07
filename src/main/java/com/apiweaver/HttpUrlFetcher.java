package com.apiweaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of UrlFetcher using Java's built-in HttpURLConnection.
 * Provides configurable timeout and user-agent settings for HTTP requests.
 */
public class HttpUrlFetcher implements UrlFetcher {
    
    private final int timeoutMs;
    private final String userAgent;
    
    /**
     * Creates a new HttpUrlFetcher with default settings.
     * Default timeout: 30 seconds, Default user-agent: "ApiWeaver/1.0"
     */
    public HttpUrlFetcher() {
        this(30000, "ApiWeaver/1.0");
    }
    
    /**
     * Creates a new HttpUrlFetcher with custom timeout and user-agent.
     * 
     * @param timeoutMs the timeout in milliseconds for HTTP requests
     * @param userAgent the user-agent string to send with requests
     */
    public HttpUrlFetcher(int timeoutMs, String userAgent) {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        if (userAgent == null || userAgent.trim().isEmpty()) {
            throw new IllegalArgumentException("User agent cannot be null or empty");
        }
        
        this.timeoutMs = timeoutMs;
        this.userAgent = userAgent;
    }
    
    @Override
    public String fetchHtmlContent(String url) throws FetchException {
        if (url == null || url.trim().isEmpty()) {
            throw new FetchException("URL cannot be null or empty");
        }
        
        try {
            URL urlObj = new URL(url.trim());
            
            // Ensure we're dealing with HTTP/HTTPS protocols
            String protocol = urlObj.getProtocol().toLowerCase();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new FetchException("Unsupported protocol: " + protocol + ". Only HTTP and HTTPS are supported.");
            }
            
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            
            // Configure connection
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setInstanceFollowRedirects(true);
            
            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new FetchException(String.format("HTTP request failed with status %d: %s", 
                    responseCode, connection.getResponseMessage()));
            }
            
            // Read response
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            return content.toString();
            
        } catch (MalformedURLException e) {
            throw new FetchException("Invalid URL format: " + url, e);
        } catch (SocketTimeoutException e) {
            throw new FetchException("Request timed out after " + timeoutMs + "ms for URL: " + url, e);
        } catch (IOException e) {
            throw new FetchException("Failed to fetch content from URL: " + url + " - " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets the configured timeout in milliseconds.
     * 
     * @return the timeout in milliseconds
     */
    public int getTimeoutMs() {
        return timeoutMs;
    }
    
    /**
     * Gets the configured user-agent string.
     * 
     * @return the user-agent string
     */
    public String getUserAgent() {
        return userAgent;
    }
}