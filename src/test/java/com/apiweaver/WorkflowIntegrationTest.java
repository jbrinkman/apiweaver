package com.apiweaver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;

/**
 * Integration tests for the complete ApiWeaver workflow orchestration.
 * Tests the coordination of all components and workflow validation.
 */
public class WorkflowIntegrationTest {

    @TempDir
    Path tempDir;
    
    private ApiWeaverCli cli;
    
    @BeforeEach
    void setUp() {
        cli = new ApiWeaverCli();
    }
    
    @Test
    void testCompleteWorkflowArgumentParsing() throws Exception {
        // Given: Command line arguments for a complete workflow
        String outputFile = tempDir.resolve("test-output.yaml").toString();
        String[] args = {"-v", "-o", outputFile, "https://example.com/api-docs"};
        
        // When: Parse arguments
        Configuration parsedConfig = cli.parseArguments(args);
        
        // Then: Verify configuration was parsed correctly
        assertNotNull(parsedConfig);
        assertEquals("https://example.com/api-docs", parsedConfig.getUrl());
        assertEquals(outputFile, parsedConfig.getOutputFile());
        assertTrue(parsedConfig.isVerbose());
        assertTrue(parsedConfig.isValid());
    }
    
    @Test
    void testWorkflowWithExistingSpecAmendment() throws Exception {
        // Given: Existing OpenAPI spec file
        Path existingSpecFile = tempDir.resolve("existing-spec.yaml");
        String existingSpecContent = "openapi: 3.1.1\n" +
            "info:\n" +
            "  title: Existing API\n" +
            "  version: 1.0.0\n" +
            "components:\n" +
            "  schemas:\n" +
            "    ExistingObject:\n" +
            "      type: object\n" +
            "      properties:\n" +
            "        existingProp:\n" +
            "          type: string\n";
        Files.writeString(existingSpecFile, existingSpecContent);
        
        // When: Parse arguments for amending existing spec
        String[] args = {
            "-e", existingSpecFile.toString(),
            "-o", tempDir.resolve("amended-output.yaml").toString(),
            "-v",
            "https://example.com/api-docs"
        };
        
        Configuration config = cli.parseArguments(args);
        
        // Then: Verify configuration includes existing spec file
        assertNotNull(config);
        assertEquals(existingSpecFile.toString(), config.getExistingSpecFile());
        assertEquals("https://example.com/api-docs", config.getUrl());
        assertTrue(config.isVerbose());
        assertTrue(config.isValid());
    }
    
    @Test
    void testWorkflowWithMultipleH2ElementsWarning() throws Exception {
        // Given: HTML with multiple H2 elements ending in "ObjectValues"
        String mockHtml = "<html>" +
            "<body>" +
                "<h2 id=\"FirstObjectValues\">First Object Properties</h2>" +
                "<table><tr><th>Property</th></tr><tr><td>prop1</td></tr></table>" +
                "<h2 id=\"SecondObjectValues\">Second Object Properties</h2>" +
                "<table><tr><th>Property</th></tr><tr><td>prop2</td></tr></table>" +
            "</body>" +
            "</html>";
        
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(mockHtml);
        List<org.jsoup.nodes.Element> h2Elements = doc.select("h2[id$=ObjectValues]");
        
        // When: Validate H2 element matching with multiple elements
        java.lang.reflect.Method validateMethod = ApiWeaverCli.class
            .getDeclaredMethod("validateH2ElementMatching", java.util.List.class);
        validateMethod.setAccessible(true);
        
        // Then: Should not throw exception but should log warning
        assertDoesNotThrow(() -> {
            validateMethod.invoke(cli, h2Elements);
        });
        
        // Verify we have multiple elements
        assertEquals(2, h2Elements.size());
    }
    
    @Test
    void testWorkflowWithNoH2ElementsFound() throws Exception {
        // Given: HTML with no H2 elements ending in "ObjectValues"
        String mockHtml = "<html>" +
            "<body>" +
                "<h2 id=\"SomeOtherHeader\">Other Header</h2>" +
                "<table><tr><th>Property</th></tr><tr><td>prop1</td></tr></table>" +
            "</body>" +
            "</html>";
        
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(mockHtml);
        List<org.jsoup.nodes.Element> h2Elements = doc.select("h2[id$=ObjectValues]");
        
        // When: Validate H2 element matching with no elements
        java.lang.reflect.Method validateMethod = ApiWeaverCli.class
            .getDeclaredMethod("validateH2ElementMatching", java.util.List.class);
        validateMethod.setAccessible(true);
        
        // Then: Should throw ExtractionException (wrapped in InvocationTargetException)
        java.lang.reflect.InvocationTargetException exception = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
            validateMethod.invoke(cli, h2Elements);
        });
        
        assertTrue(exception.getCause() instanceof ExtractionException);
        assertTrue(exception.getCause().getMessage().contains("No H2 elements found"));
    }
    
    @Test
    void testProgressReporting() throws Exception {
        // Given: Progress reporting method
        java.lang.reflect.Method reportProgressMethod = ApiWeaverCli.class
            .getDeclaredMethod("reportProgress", String.class, boolean.class);
        reportProgressMethod.setAccessible(true);
        
        // When/Then: Should not throw exceptions
        assertDoesNotThrow(() -> {
            reportProgressMethod.invoke(cli, "Test progress message", true);
            reportProgressMethod.invoke(cli, "Silent progress message", false);
        });
    }
    
    @Test
    void testPropertyConversion() throws Exception {
        // Given: List of PropertyDefinition objects
        List<PropertyDefinition> properties = createMockProperties();
        
        java.lang.reflect.Method convertMethod = ApiWeaverCli.class
            .getDeclaredMethod("convertToOpenApiProperties", java.util.List.class);
        convertMethod.setAccessible(true);
        
        // When: Convert properties
        @SuppressWarnings("unchecked")
        List<OpenApiProperty> result = (List<OpenApiProperty>) convertMethod.invoke(cli, properties);
        
        // Then: Should convert successfully
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    void testYamlConversion() throws Exception {
        // Given: OpenApiSpec object
        OpenApiSpec spec = createMockOpenApiSpec();
        
        java.lang.reflect.Method convertMethod = ApiWeaverCli.class
            .getDeclaredMethod("convertSpecToYaml", OpenApiSpec.class);
        convertMethod.setAccessible(true);
        
        // When: Convert to YAML
        String yamlResult = (String) convertMethod.invoke(cli, spec);
        
        // Then: Should produce valid YAML
        assertNotNull(yamlResult);
        assertTrue(yamlResult.contains("openapi:"));
        assertTrue(yamlResult.contains("3.1.1"));
    }
    
    // Helper methods
    
    private List<PropertyDefinition> createMockProperties() {
        List<PropertyDefinition> properties = new ArrayList<>();
        
        PropertyDefinition prop1 = new PropertyDefinition(
            "id", "integer", true, false, "Unique identifier");
        properties.add(prop1);
        
        PropertyDefinition prop2 = new PropertyDefinition(
            "name", "string", true, true, "Object name");
        properties.add(prop2);
        
        return properties;
    }
    
    private OpenApiSpec createMockOpenApiSpec() {
        OpenApiSpec spec = new OpenApiSpec();
        spec.setOpenapi("3.1.1");
        
        OpenApiSpec.Info info = new OpenApiSpec.Info();
        info.setTitle("Test API");
        info.setVersion("1.0.0");
        spec.setInfo(info);
        
        return spec;
    }
}
