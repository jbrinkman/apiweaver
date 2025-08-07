package com.apiweaver;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Extracts property definitions from HTML tables in TimeTap API documentation.
 * Handles column identification with fuzzy matching and row parsing.
 */
public class PropertyTableExtractor implements TableExtractor {
    
    // Expected column names with variations for fuzzy matching
    private static final Map<String, Pattern> COLUMN_PATTERNS = new HashMap<>();
    
    static {
        COLUMN_PATTERNS.put("name", Pattern.compile("property\\s*name", Pattern.CASE_INSENSITIVE));
        COLUMN_PATTERNS.put("type", Pattern.compile("type", Pattern.CASE_INSENSITIVE));
        COLUMN_PATTERNS.put("required", Pattern.compile("required", Pattern.CASE_INSENSITIVE));
        COLUMN_PATTERNS.put("writable", Pattern.compile("writable", Pattern.CASE_INSENSITIVE));
        COLUMN_PATTERNS.put("description", Pattern.compile("description", Pattern.CASE_INSENSITIVE));
    }
    
    @Override
    public List<PropertyDefinition> extractProperties(Element table) throws ExtractionException {
        if (table == null) {
            throw new ExtractionException("Table element cannot be null");
        }
        
        if (!"table".equals(table.tagName().toLowerCase())) {
            throw new ExtractionException("Element is not a table: " + table.tagName());
        }
        
        Elements rows = table.select("tr");
        if (rows.isEmpty()) {
            throw new ExtractionException("Table contains no rows");
        }
        
        // Identify column positions
        Map<String, Integer> columnMap = identifyColumns(table);
        validateRequiredColumns(columnMap);
        
        List<PropertyDefinition> properties = new ArrayList<>();
        
        // Skip header row(s) and process data rows
        boolean headerProcessed = false;
        for (Element row : rows) {
            if (!headerProcessed) {
                // Skip the first row that contains headers
                headerProcessed = true;
                continue;
            }
            
            try {
                PropertyDefinition property = parseRow(row, columnMap);
                if (property != null && property.isValid()) {
                    properties.add(property);
                }
            } catch (Exception e) {
                // Log warning and continue with next row
                System.err.println("Warning: Failed to parse table row, skipping: " + e.getMessage());
            }
        }
        
        if (properties.isEmpty()) {
            throw new ExtractionException("No valid properties extracted from table");
        }
        
        return properties;
    }
    
    /**
     * Identifies column positions in the table using fuzzy matching.
     */
    private Map<String, Integer> identifyColumns(Element table) throws ExtractionException {
        Elements headerRows = table.select("tr");
        if (headerRows.isEmpty()) {
            throw new ExtractionException("Table has no header row");
        }
        
        // Try to find header row (usually the first row)
        Element headerRow = headerRows.first();
        Elements headerCells = headerRow.select("th, td");
        
        if (headerCells.isEmpty()) {
            throw new ExtractionException("Header row contains no cells");
        }
        
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (int i = 0; i < headerCells.size(); i++) {
            String headerText = headerCells.get(i).text().trim();
            
            // Try to match each column pattern
            for (Map.Entry<String, Pattern> entry : COLUMN_PATTERNS.entrySet()) {
                if (entry.getValue().matcher(headerText).find()) {
                    columnMap.put(entry.getKey(), i);
                    break;
                }
            }
        }
        
        return columnMap;
    }
    
    /**
     * Validates that all required columns are present.
     */
    private void validateRequiredColumns(Map<String, Integer> columnMap) throws ExtractionException {
        List<String> missingColumns = new ArrayList<>();
        
        for (String requiredColumn : Arrays.asList("name", "type")) {
            if (!columnMap.containsKey(requiredColumn)) {
                missingColumns.add(requiredColumn);
            }
        }
        
        if (!missingColumns.isEmpty()) {
            throw new ExtractionException("Missing required columns: " + String.join(", ", missingColumns));
        }
    }
    
    /**
     * Parses a single table row to extract property information.
     */
    private PropertyDefinition parseRow(Element row, Map<String, Integer> columnMap) {
        Elements cells = row.select("td, th");
        
        if (cells.isEmpty()) {
            return null;
        }
        
        // Extract required fields
        String name = extractCellText(cells, columnMap.get("name"));
        String type = extractCellText(cells, columnMap.get("type"));
        
        if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()) {
            return null;
        }
        
        // Extract optional fields with defaults
        boolean required = parseBooleanValue(extractCellText(cells, columnMap.get("required")), false);
        boolean writable = parseBooleanValue(extractCellText(cells, columnMap.get("writable")), true);
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
     * Extracts and cleans description text from a cell.
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
    private boolean parseBooleanValue(String text, boolean defaultValue) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        
        String normalized = text.trim().toLowerCase();
        
        // Handle common boolean representations
        switch (normalized) {
            case "true":
            case "yes":
            case "y":
            case "1":
            case "required":
            case "mandatory":
                return true;
            case "false":
            case "no":
            case "n":
            case "0":
            case "optional":
            case "not required":
                return false;
            default:
                return defaultValue;
        }
    }
}