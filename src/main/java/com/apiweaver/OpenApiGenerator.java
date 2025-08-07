package com.apiweaver;

import java.io.IOException;
import java.util.List;

/**
 * Interface for generating or amending OpenAPI specification files.
 * Handles creation of OpenAPI 3.1.1 compliant schemas.
 */
public interface OpenApiGenerator {
    
    /**
     * Generates a new OpenAPI specification or amends an existing one with new properties.
     * 
     * @param properties the list of OpenAPI properties to include
     * @param existing the existing OpenAPI specification to amend, or null for new spec
     * @return the generated or amended OpenAPI specification
     */
    OpenApiSpec generateOrAmendSpec(List<OpenApiProperty> properties, OpenApiSpec existing);
    
    /**
     * Creates a new empty OpenAPI 3.1.1 specification.
     * 
     * @return a new OpenAPI specification with basic structure
     */
    OpenApiSpec createNewSpec();
    
    /**
     * Loads an existing OpenAPI specification from a file.
     * 
     * @param filePath the path to the OpenAPI file
     * @return the loaded OpenAPI specification
     * @throws IOException if the file cannot be read or parsed
     */
    OpenApiSpec loadExistingSpec(String filePath) throws IOException;
}