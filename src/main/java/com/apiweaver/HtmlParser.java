package com.apiweaver;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    Document parseHtml(String htmlContent);
    
    /**
     * Finds H2 elements with id attributes ending in the specified suffix.
     * 
     * @param doc the parsed document
     * @param suffix the suffix to match in id attributes
     * @return list of matching H2 elements
     */
    List<Element> findH2ElementsWithIdEndingIn(Document doc, String suffix);
    
    /**
     * Finds the first table element that follows the given element in the DOM.
     * 
     * @param doc the parsed document
     * @param element the element to search after
     * @return the first table element found, or null if none exists
     */
    Element findFirstTableAfterElement(Document doc, Element element);
}