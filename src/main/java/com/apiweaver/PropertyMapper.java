package com.apiweaver;

/**
 * Interface for mapping HTML property definitions to OpenAPI properties.
 * Handles type conversion and property attribute mapping.
 */
public interface PropertyMapper {
    
    /**
     * Maps a property definition from HTML to an OpenAPI property.
     * 
     * @param property the property definition extracted from HTML
     * @return the corresponding OpenAPI property
     */
    OpenApiProperty mapToOpenApiProperty(PropertyDefinition property);
    
    /**
     * Maps HTML property types to OpenAPI data types.
     * 
     * @param htmlType the type string from HTML documentation
     * @return the corresponding OpenAPI type string
     */
    String mapHtmlTypeToOpenApiType(String htmlType);
}