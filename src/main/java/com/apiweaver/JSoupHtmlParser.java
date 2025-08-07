package com.apiweaver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

/**
 * JSoup-based implementation of HtmlParser for parsing HTML content
 * and extracting specific elements from TimeTap API documentation.
 */
public class JSoupHtmlParser implements HtmlParser {
    
    /**
     * Parses HTML content into a JSoup Document.
     * 
     * @param htmlContent the HTML content to parse
     * @return parsed JSoup Document
     */
    @Override
    public Document parseHtml(String htmlContent) {
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("HTML content cannot be null or empty");
        }
        return Jsoup.parse(htmlContent);
    }
    
    /**
     * Finds H2 elements with id attributes ending in the specified suffix.
     * This method is specifically designed to find H2 elements with ids ending in "ObjectValues"
     * as per the TimeTap API documentation structure.
     * 
     * @param doc the parsed document
     * @param suffix the suffix to match in id attributes (e.g., "ObjectValues")
     * @return list of matching H2 elements
     */
    @Override
    public List<Element> findH2ElementsWithIdEndingIn(Document doc, String suffix) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (suffix == null || suffix.trim().isEmpty()) {
            throw new IllegalArgumentException("Suffix cannot be null or empty");
        }
        
        List<Element> matchingElements = new ArrayList<>();
        Elements h2Elements = doc.select("h2[id]");
        
        for (Element h2 : h2Elements) {
            String id = h2.attr("id");
            if (id != null && id.endsWith(suffix)) {
                matchingElements.add(h2);
            }
        }
        
        return matchingElements;
    }
    
    /**
     * Finds the first table element that follows the given element in the DOM.
     * This method searches for the first table that appears after the specified element
     * in document order, which is used to locate property tables after H2 headers.
     * 
     * @param doc the parsed document
     * @param element the element to search after
     * @return the first table element found, or null if none exists
     */
    @Override
    public Element findFirstTableAfterElement(Document doc, Element element) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        
        // Get all elements that come after the given element in document order
        Elements allElements = doc.getAllElements();
        boolean foundStartElement = false;
        
        for (Element currentElement : allElements) {
            if (foundStartElement && "table".equals(currentElement.tagName())) {
                return currentElement;
            }
            
            if (currentElement.equals(element)) {
                foundStartElement = true;
            }
        }
        
        return null;
    }
}