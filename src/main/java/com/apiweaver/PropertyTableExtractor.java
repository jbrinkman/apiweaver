package com.apiweaver;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Extracts property definitions from HTML tables using JSoup.
 * Handles column identification with fuzzy matching and row parsing.
 */
public class PropertyTableExtractor implements TableExtractor {
    
    // Expected column names with variations for fuzzy matching
    private static final Map<String, List<String>> COLUMN_PATTERNS = Map.of(
        "name", Arrays.asList("property name", "name", "property", "field"),
        "type", Arrays.asList("type", "data type", "datatype", "format"),
        "required", Arrays.asList("required", "req", "mandatory", "required?"),
        "writable", Arrays.asList("writable", "write", "editable", "writable?"),
        "description", Arrays.asList("description", "desc", "details", "notes")
    );
    
    @Override
    public List<PropertyDefinition> extractProperties(Object table) throws ExtractionException {
        if (!(table instanceof Element)) {
            throw new ExtractionException("Expected JSoup Element but got: " + 
                (table != null ? table.getClass().getSimpleName() : "null"));
        }
        
        Element tableElement = (Element) table;
        
        // Find table headers
        Map<String, Integer> columnMap = identifyColumns(tableElement);
        validateRequiredColumns(columnMap);
        
        // Extract rows
        Elements rows = tableElement.select("tbody tr, tr");
        if (rows.isEmpty()) {
            throw new ExtractionException("No table rows found");
        }
        
        List<PropertyDefinition> properties = new ArrayList<>();
        
        // Skip header row if it exists or if first row looks like a header
        int startIndex = (hasHeaderRow(tableElement) || firstRowLooksLikeHeader(tableElement)) ? 1 : 0;
        
        for (int i = startIndex; i < rows.size(); i++) {
            Element row = rows.get(i);
            try {
                PropertyDefinition property = parseRow(row, columnMap);
                if (property != null && property.isValid()) {
                    properties.add(property);
                }
            } catch (Exception e) {
                // Log warning and continue with next row
                System.err.println("Warning: Skipping malformed row " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        if (properties.isEmpty()) {
            throw new ExtractionException("No valid properties extracted from table");
        }
        
        return properties;
    }
    
    /**
     * Identifies column positions using fuzzy matching.
     */
    private Map<String, Integer> identifyColumns(Element table) throws ExtractionException {
        Map<String, Integer> columnMap = new HashMap<>();
        
        // Try to find header row
        Elements headerRows = table.select("thead tr, tr:first-child");
        if (headerRows.isEmpty()) {
            throw new ExtractionException("No header row found in table");
        }
        
        Element headerRow = headerRows.first();
        Elements headers = headerRow.select("th, td");
        
        if (headers.isEmpty()) {
            throw new ExtractionException("No header cells found in table");
        }
        
        // Match headers to expected columns
        for (int i = 0; i < headers.size(); i++) {
            String headerText = headers.get(i).text().toLowerCase().trim();
            
            for (Map.Entry<String, List<String>> entry : COLUMN_PATTERNS.entrySet()) {
                String columnType = entry.getKey();
                List<String> patterns = entry.getValue();
                
                // Check if any pattern matches this header
                boolean matched = false;
                for (String pattern : patterns) {
                    if (headerText.contains(pattern) || fuzzyMatch(headerText, pattern)) {
                        columnMap.put(columnType, i);
                        matched = true;
                        break;
                    }
                }
                
                if (matched) {
                    break;
                }
            }
        }
        
        return columnMap;
    }
    
    /**
     * Checks if the first row looks like a header row based on content.
     */
    private boolean firstRowLooksLikeHeader(Element table) {
        Elements firstRowCells = table.select("tr:first-child td, tr:first-child th");
        if (firstRowCells.isEmpty()) {
            return false;
        }
        
        // Check if first row contains header-like text
        int headerLikeCount = 0;
        for (Element cell : firstRowCells) {
            String cellText = cell.text().toLowerCase().trim();
            for (List<String> patterns : COLUMN_PATTERNS.values()) {
                if (patterns.stream().anyMatch(pattern -> 
                    cellText.contains(pattern) || fuzzyMatch(cellText, pattern))) {
                    headerLikeCount++;
                    break;
                }
            }
        }
        
        // If more than half the cells look like headers, treat first row as header
        return headerLikeCount > firstRowCells.size() / 2;
    }
    
    /**
     * Validates that required columns were found.
     */
    private void validateRequiredColumns(Map<String, Integer> columnMap) throws ExtractionException {
        List<String> requiredColumns = Arrays.asList("name", "type");
        List<String> missingColumns = requiredColumns.stream()
            .filter(col -> !columnMap.containsKey(col))
            .collect(Collectors.toList());
        
        if (!missingColumns.isEmpty()) {
            throw new ExtractionException("Required columns not found: " + missingColumns);
        }
    }
    
    /**
     * Checks if the table has a dedicated header row.
     */
    private boolean hasHeaderRow(Element table) {
        return !table.select("thead").isEmpty() || 
               !table.select("tr:first-child th").isEmpty();
    }
    
    /**
     * Parses a single table row into a PropertyDefinition.
     */
    private PropertyDefinition parseRow(Element row, Map<String, Integer> columnMap) {
        Elements cells = row.select("td, th");
        
        if (cells.isEmpty()) {
            return null;
        }
        
        // Extract required fields
        String name = extractCellText(cells, columnMap.get("name"));
        String type = extractCellText(cells, columnMap.get("type"));
        
        if (name == null || name.trim().isEmpty() || 
            type == null || type.trim().isEmpty()) {
            return null;
        }
        
        // Extract optional fields
        String requiredText = extractCellText(cells, columnMap.get("required"));
        String writableText = extractCellText(cells, columnMap.get("writable"));
        
        boolean required = parseBooleanValue(requiredText);
        boolean writable = parseBooleanValue(writableText);
        String description = extractDescription(cells, columnMap.get("description"));
        

        
        return new PropertyDefinition(
            name.trim(),
            type.trim(),
            required,
            writable,
            description
        );
    }
    
    /**
     * Extracts text from a cell at the specified column index.
     */
    private String extractCellText(Elements cells, Integer columnIndex) {
        if (columnIndex == null || columnIndex >= cells.size()) {
            return null;
        }
        
        Element cell = cells.get(columnIndex);
        return cell.text().trim();
    }
    
    /**
     * Extracts and cleans description text.
     */
    private String extractDescription(Elements cells, Integer columnIndex) {
        String description = extractCellText(cells, columnIndex);
        if (description == null || description.isEmpty()) {
            return "";
        }
        
        // Clean up common HTML artifacts and normalize whitespace
        return description.replaceAll("\\s+", " ").trim();
    }
    
    /**
     * Parses boolean values from text with common variations.
     */
    private boolean parseBooleanValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        String normalized = value.toLowerCase().trim();
        return normalized.equals("true") || 
               normalized.equals("yes") || 
               normalized.equals("y") ||
               normalized.equals("1") ||
               normalized.equals("required") ||
               normalized.equals("mandatory");
    }
    
    /**
     * Performs fuzzy matching between two strings.
     * Uses simple similarity based on common characters.
     */
    private boolean fuzzyMatch(String text, String pattern) {
        if (text == null || pattern == null) {
            return false;
        }
        
        // Simple fuzzy matching - check if pattern is contained or has high similarity
        if (text.contains(pattern) || pattern.contains(text)) {
            return true;
        }
        
        // Calculate similarity based on common characters, but only for similar length strings
        if (Math.abs(text.length() - pattern.length()) > Math.max(text.length(), pattern.length()) / 2) {
            return false; // Too different in length
        }
        
        Set<Character> textChars = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toSet());
        Set<Character> patternChars = pattern.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toSet());
        
        Set<Character> intersection = new HashSet<>(textChars);
        intersection.retainAll(patternChars);
        
        double similarity = (double) intersection.size() / Math.max(textChars.size(), patternChars.size());
        return similarity > 0.7; // 70% similarity threshold
    }
}