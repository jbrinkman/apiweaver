package com.apiweaver;

import java.util.List;

/**
 * Interface for extracting property information from HTML tables.
 * Handles parsing of table structures to extract API property definitions.
 */
public interface TableExtractor {
    
    /**
     * Extracts property definitions from an HTML table element.
     * 
     * @param table the HTML table element to extract from
     * @return list of property definitions extracted from the table
     * @throws ExtractionException if the table structure is invalid or cannot be parsed
     */
    List<PropertyDefinition> extractProperties(Object table) throws ExtractionException;
}