package com.apiweaver;

import java.util.List;

/**
 * Interface for parsing HTML content and extracting specific elements.
 * Provides abstraction for HTML parsing operations.
 */
public interface HtmlParser {
    
    /**
     * Parses HTML content into a document structure.
     * 
     * @param htmlContent the HTML content to parse
     * @return parsed document object
     */
    Object parseHtml(String htmlContent);
    
    /**
     * Finds H2 elements with id attributes ending in the specified suffix.
     * 
     * @param doc the parsed document
     * @param suffix the suffix to match in id attributes
     * @return list of matching H2 elements
     */
    List<Object> findH2ElementsWithIdEndingIn(Object doc, String suffix);
    
    /**
     * Finds the first table element that follows the given element in the DOM.
     * 
     * @param doc the parsed document
     * @param element the element to search after
     * @return the first table element found, or null if none exists
     */
    Object findFirstTableAfterElement(Object doc, Object element);
}