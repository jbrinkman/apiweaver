package com.apiweaver;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of PropertyMapper for TimeTap API documentation.
 * Maps HTML property types to OpenAPI 3.1.1 compliant types and formats.
 */
public class TimeTapPropertyMapper implements PropertyMapper {
    
    /**
     * Mapping of TimeTap HTML types to OpenAPI types and formats.
     * Key: HTML type string, Value: array of [type, format] where format can be null.
     */
    private static final Map<String, String[]> TYPE_MAPPINGS = new HashMap<>();
    
    static {
        // String types
        TYPE_MAPPINGS.put("string", new String[]{"string", null});
        TYPE_MAPPINGS.put("String", new String[]{"string", null});
        TYPE_MAPPINGS.put("text", new String[]{"string", null});
        TYPE_MAPPINGS.put("Text", new String[]{"string", null});
        
        // Integer types
        TYPE_MAPPINGS.put("integer", new String[]{"integer", null});
        TYPE_MAPPINGS.put("Integer", new String[]{"integer", null});
        TYPE_MAPPINGS.put("int", new String[]{"integer", "int32"});
        TYPE_MAPPINGS.put("Int", new String[]{"integer", "int32"});
        TYPE_MAPPINGS.put("long", new String[]{"integer", "int64"});
        TYPE_MAPPINGS.put("Long", new String[]{"integer", "int64"});
        
        // Number types
        TYPE_MAPPINGS.put("number", new String[]{"number", null});
        TYPE_MAPPINGS.put("Number", new String[]{"number", null});
        TYPE_MAPPINGS.put("decimal", new String[]{"number", null});
        TYPE_MAPPINGS.put("Decimal", new String[]{"number", null});
        TYPE_MAPPINGS.put("float", new String[]{"number", "float"});
        TYPE_MAPPINGS.put("Float", new String[]{"number", "float"});
        TYPE_MAPPINGS.put("double", new String[]{"number", "double"});
        TYPE_MAPPINGS.put("Double", new String[]{"number", "double"});
        
        // Boolean types
        TYPE_MAPPINGS.put("boolean", new String[]{"boolean", null});
        TYPE_MAPPINGS.put("Boolean", new String[]{"boolean", null});
        TYPE_MAPPINGS.put("bool", new String[]{"boolean", null});
        TYPE_MAPPINGS.put("Bool", new String[]{"boolean", null});
        
        // Date/Time types
        TYPE_MAPPINGS.put("date", new String[]{"string", "date"});
        TYPE_MAPPINGS.put("Date", new String[]{"string", "date"});
        TYPE_MAPPINGS.put("datetime", new String[]{"string", "date-time"});
        TYPE_MAPPINGS.put("DateTime", new String[]{"string", "date-time"});
        TYPE_MAPPINGS.put("timestamp", new String[]{"string", "date-time"});
        TYPE_MAPPINGS.put("Timestamp", new String[]{"string", "date-time"});
        
        // Array types
        TYPE_MAPPINGS.put("array", new String[]{"array", null});
        TYPE_MAPPINGS.put("Array", new String[]{"array", null});
        TYPE_MAPPINGS.put("list", new String[]{"array", null});
        TYPE_MAPPINGS.put("List", new String[]{"array", null});
        
        // Object types
        TYPE_MAPPINGS.put("object", new String[]{"object", null});
        TYPE_MAPPINGS.put("Object", new String[]{"object", null});
        
        // Common TimeTap specific types
        TYPE_MAPPINGS.put("id", new String[]{"integer", "int64"});
        TYPE_MAPPINGS.put("ID", new String[]{"integer", "int64"});
        TYPE_MAPPINGS.put("uuid", new String[]{"string", "uuid"});
        TYPE_MAPPINGS.put("UUID", new String[]{"string", "uuid"});
        TYPE_MAPPINGS.put("email", new String[]{"string", "email"});
        TYPE_MAPPINGS.put("Email", new String[]{"string", "email"});
        TYPE_MAPPINGS.put("url", new String[]{"string", "uri"});
        TYPE_MAPPINGS.put("URL", new String[]{"string", "uri"});
        TYPE_MAPPINGS.put("uri", new String[]{"string", "uri"});
        TYPE_MAPPINGS.put("URI", new String[]{"string", "uri"});
    }
    
    @Override
    public OpenApiProperty mapToOpenApiProperty(PropertyDefinition property) {
        if (property == null) {
            throw new IllegalArgumentException("Property definition cannot be null");
        }
        
        if (!property.isValid()) {
            throw new IllegalArgumentException("Property definition is not valid: " + property);
        }
        
        String openApiType = mapHtmlTypeToOpenApiType(property.getType());
        String format = getFormatForType(property.getType());
        
        return OpenApiProperty.builder(property.getName(), openApiType)
                .format(format)
                .required(property.isRequired())
                .readOnly(!property.isWritable())
                .description(property.getDescription())
                .build();
    }
    
    @Override
    public String mapHtmlTypeToOpenApiType(String htmlType) {
        if (htmlType == null || htmlType.trim().isEmpty()) {
            return "string"; // Default fallback type
        }
        
        String trimmedType = htmlType.trim();
        
        // Direct mapping lookup
        String[] mapping = TYPE_MAPPINGS.get(trimmedType);
        if (mapping != null) {
            return mapping[0];
        }
        
        // Handle array types with brackets (e.g., "string[]", "Array[String]")
        if (trimmedType.contains("[]") || trimmedType.contains("[") && trimmedType.contains("]")) {
            return "array";
        }
        
        // Handle nullable types (e.g., "string?", "Integer?")
        if (trimmedType.endsWith("?")) {
            String baseType = trimmedType.substring(0, trimmedType.length() - 1);
            return mapHtmlTypeToOpenApiType(baseType);
        }
        
        // Case-insensitive fallback for common types
        String lowerType = trimmedType.toLowerCase();
        if (lowerType.contains("string") || lowerType.contains("text")) {
            return "string";
        } else if (lowerType.contains("int") || lowerType.contains("long")) {
            return "integer";
        } else if (lowerType.contains("float") || lowerType.contains("double") || lowerType.contains("decimal")) {
            return "number";
        } else if (lowerType.contains("bool")) {
            return "boolean";
        } else if (lowerType.contains("date") || lowerType.contains("time")) {
            return "string";
        } else if (lowerType.contains("array") || lowerType.contains("list")) {
            return "array";
        } else if (lowerType.contains("object")) {
            return "object";
        }
        
        // Default fallback
        return "string";
    }
    
    /**
     * Gets the OpenAPI format for a given HTML type.
     * 
     * @param htmlType the HTML type string
     * @return the OpenAPI format string, or null if no specific format applies
     */
    private String getFormatForType(String htmlType) {
        if (htmlType == null || htmlType.trim().isEmpty()) {
            return null;
        }
        
        String trimmedType = htmlType.trim();
        
        // Direct mapping lookup
        String[] mapping = TYPE_MAPPINGS.get(trimmedType);
        if (mapping != null) {
            return mapping[1]; // Format is the second element
        }
        
        // Handle nullable types
        if (trimmedType.endsWith("?")) {
            String baseType = trimmedType.substring(0, trimmedType.length() - 1);
            return getFormatForType(baseType);
        }
        
        return null;
    }
}