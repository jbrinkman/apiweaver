package com.apiweaver;

/**
 * Interface for fetching HTML content from URLs.
 * Provides abstraction for HTTP operations to enable testing and different implementations.
 */
public interface UrlFetcher {
    
    /**
     * Fetches HTML content from the specified URL.
     * 
     * @param url the URL to fetch content from
     * @return the HTML content as a string
     * @throws FetchException if the URL cannot be fetched or is invalid
     */
    String fetchHtmlContent(String url) throws FetchException;
}