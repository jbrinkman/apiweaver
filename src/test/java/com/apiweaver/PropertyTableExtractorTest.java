package com.apiweaver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropertyTableExtractorTest {
    
    private PropertyTableExtractor extractor;
    
    @BeforeEach
    void setUp() {
        extractor = new PropertyTableExtractor();
    }
    
    @Test
    void testExtractProperties_ValidTable() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
                "<th>Required</th>" +
                "<th>Writable</th>" +
                "<th>Description</th>" +
            "</tr>" +
            "<tr>" +
                "<td>id</td>" +
                "<td>integer</td>" +
                "<td>true</td>" +
                "<td>false</td>" +
                "<td>Unique identifier</td>" +
            "</tr>" +
            "<tr>" +
                "<td>name</td>" +
                "<td>string</td>" +
                "<td>yes</td>" +
                "<td>true</td>" +
                "<td>Object name</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(2, properties.size());
        
        PropertyDefinition idProperty = properties.get(0);
        assertEquals("id", idProperty.getName());
        assertEquals("integer", idProperty.getType());
        assertTrue(idProperty.isRequired());
        assertFalse(idProperty.isWritable());
        assertEquals("Unique identifier", idProperty.getDescription());
        
        PropertyDefinition nameProperty = properties.get(1);
        assertEquals("name", nameProperty.getName());
        assertEquals("string", nameProperty.getType());
        assertTrue(nameProperty.isRequired());
        assertTrue(nameProperty.isWritable());
        assertEquals("Object name", nameProperty.getDescription());
    }
    
    @Test
    void testExtractProperties_FuzzyColumnMatching() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>TYPE</th>" +
                "<th>REQUIRED</th>" +
                "<th>writable</th>" +
                "<th>Description</th>" +
            "</tr>" +
            "<tr>" +
                "<td>status</td>" +
                "<td>boolean</td>" +
                "<td>no</td>" +
                "<td>yes</td>" +
                "<td>Status flag</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        PropertyDefinition property = properties.get(0);
        assertEquals("status", property.getName());
        assertEquals("boolean", property.getType());
        assertFalse(property.isRequired());
        assertTrue(property.isWritable());
    }
    
    @Test
    void testExtractProperties_MinimalColumns() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
            "</tr>" +
            "<tr>" +
                "<td>count</td>" +
                "<td>number</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        PropertyDefinition property = properties.get(0);
        assertEquals("count", property.getName());
        assertEquals("number", property.getType());
        assertFalse(property.isRequired()); // default value
        assertTrue(property.isWritable()); // default value
        assertEquals("", property.getDescription()); // default value
    }
    
    @Test
    void testExtractProperties_BooleanVariations() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
                "<th>Required</th>" +
                "<th>Writable</th>" +
            "</tr>" +
            "<tr>" +
                "<td>prop1</td>" +
                "<td>string</td>" +
                "<td>1</td>" +
                "<td>0</td>" +
            "</tr>" +
            "<tr>" +
                "<td>prop2</td>" +
                "<td>string</td>" +
                "<td>mandatory</td>" +
                "<td>no</td>" +
            "</tr>" +
            "<tr>" +
                "<td>prop3</td>" +
                "<td>string</td>" +
                "<td>optional</td>" +
                "<td>y</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(3, properties.size());
        
        assertTrue(properties.get(0).isRequired());
        assertFalse(properties.get(0).isWritable());
        
        assertTrue(properties.get(1).isRequired());
        assertFalse(properties.get(1).isWritable());
        
        assertFalse(properties.get(2).isRequired());
        assertTrue(properties.get(2).isWritable());
    }
    
    @Test
    void testExtractProperties_SkipMalformedRows() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
            "</tr>" +
            "<tr>" +
                "<td>valid_prop</td>" +
                "<td>string</td>" +
            "</tr>" +
            "<tr>" +
                "<td></td>" +
                "<td>string</td>" +
            "</tr>" +
            "<tr>" +
                "<td>another_valid</td>" +
                "<td>integer</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(2, properties.size());
        assertEquals("valid_prop", properties.get(0).getName());
        assertEquals("another_valid", properties.get(1).getName());
    }
    
    @Test
    void testExtractProperties_NullTable() {
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(null);
        });
        assertEquals("Table element cannot be null", exception.getMessage());
    }
    
    @Test
    void testExtractProperties_NotTableElement() {
        Element div = Jsoup.parse("<div>Not a table</div>").select("div").first();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(div);
        });
        assertEquals("Element is not a table: div", exception.getMessage());
    }
    
    @Test
    void testExtractProperties_EmptyTable() {
        Element table = Jsoup.parse("<table></table>").select("table").first();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(table);
        });
        assertEquals("Table contains no rows", exception.getMessage());
    }
    
    @Test
    void testExtractProperties_MissingRequiredColumns() {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Description</th>" +
            "</tr>" +
            "<tr>" +
                "<td>prop1</td>" +
                "<td>Some description</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(table);
        });
        assertTrue(exception.getMessage().contains("Missing required columns"));
        assertTrue(exception.getMessage().contains("type"));
    }
    
    @Test
    void testExtractProperties_NoValidProperties() {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
            "</tr>" +
            "<tr>" +
                "<td></td>" +
                "<td></td>" +
            "</tr>" +
            "<tr>" +
                "<td>   </td>" +
                "<td>   </td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        
        ExtractionException exception = assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(table);
        });
        assertEquals("No valid properties extracted from table", exception.getMessage());
    }
    
    @Test
    void testExtractProperties_WithTdHeaders() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<td><strong>Property Name</strong></td>" +
                "<td><strong>Type</strong></td>" +
                "<td><strong>Required</strong></td>" +
            "</tr>" +
            "<tr>" +
                "<td>timestamp</td>" +
                "<td>datetime</td>" +
                "<td>true</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        PropertyDefinition property = properties.get(0);
        assertEquals("timestamp", property.getName());
        assertEquals("datetime", property.getType());
        assertTrue(property.isRequired());
    }
    
    @Test
    void testExtractProperties_DescriptionCleaning() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
                "<th>Description</th>" +
            "</tr>" +
            "<tr>" +
                "<td>prop1</td>" +
                "<td>string</td>" +
                "<td>  This   is   a   description   with   extra   spaces  </td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        assertEquals("This is a description with extra spaces", properties.get(0).getDescription());
    }
    
    @Test
    void testExtractProperties_MissingCells() throws ExtractionException {
        String html = "<table>" +
            "<tr>" +
                "<th>Property Name</th>" +
                "<th>Type</th>" +
                "<th>Required</th>" +
                "<th>Writable</th>" +
                "<th>Description</th>" +
            "</tr>" +
            "<tr>" +
                "<td>incomplete_prop</td>" +
                "<td>string</td>" +
                "<!-- Missing cells -->" +
            "</tr>" +
            "<tr>" +
                "<td>complete_prop</td>" +
                "<td>integer</td>" +
                "<td>yes</td>" +
                "<td>no</td>" +
                "<td>Complete property</td>" +
            "</tr>" +
            "</table>";
        
        Element table = Jsoup.parse(html).select("table").first();
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(2, properties.size());
        
        // First property should use defaults for missing cells
        PropertyDefinition incompleteProperty = properties.get(0);
        assertEquals("incomplete_prop", incompleteProperty.getName());
        assertEquals("string", incompleteProperty.getType());
        assertFalse(incompleteProperty.isRequired()); // default
        assertTrue(incompleteProperty.isWritable()); // default
        assertEquals("", incompleteProperty.getDescription()); // default
        
        // Second property should have all values
        PropertyDefinition completeProperty = properties.get(1);
        assertEquals("complete_prop", completeProperty.getName());
        assertEquals("integer", completeProperty.getType());
        assertTrue(completeProperty.isRequired());
        assertFalse(completeProperty.isWritable());
        assertEquals("Complete property", completeProperty.getDescription());
    }
}