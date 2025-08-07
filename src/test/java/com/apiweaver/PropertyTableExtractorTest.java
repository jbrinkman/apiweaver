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
    void testExtractProperties_StandardTable() throws ExtractionException {
        String html = "<table>" +
            "<thead>" +
                "<tr>" +
                    "<th>Property Name</th>" +
                    "<th>Type</th>" +
                    "<th>Required</th>" +
                    "<th>Writable</th>" +
                    "<th>Description</th>" +
                "</tr>" +
            "</thead>" +
            "<tbody>" +
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
            "</tbody>" +
        "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
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
                    "<th>Field</th>" +
                    "<th>Data Type</th>" +
                    "<th>Req</th>" +
                    "<th>Editable</th>" +
                    "<th>Notes</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>status</td>" +
                    "<td>boolean</td>" +
                    "<td>no</td>" +
                    "<td>yes</td>" +
                    "<td>Current status</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        
        PropertyDefinition property = properties.get(0);
        assertEquals("status", property.getName());
        assertEquals("boolean", property.getType());
        assertFalse(property.isRequired());


        assertTrue(property.isWritable());
        assertEquals("Current status", property.getDescription());
    }
    
    @Test
    void testExtractProperties_MissingOptionalColumns() throws ExtractionException {
        String html = "<table>" +
                "<tr>" +
                    "<th>Property Name</th>" +
                    "<th>Type</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>email</td>" +
                    "<td>string</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        
        PropertyDefinition property = properties.get(0);
        assertEquals("email", property.getName());
        assertEquals("string", property.getType());
        assertFalse(property.isRequired()); // Default value
        assertFalse(property.isWritable()); // Default value
        assertEquals("", property.getDescription()); // Default value
    }
    
    @Test
    void testExtractProperties_VariousBooleanFormats() throws ExtractionException {
        String html = "<table>" +
                "<tr>" +
                    "<th>Name</th>" +
                    "<th>Type</th>" +
                    "<th>Required</th>" +
                    "<th>Writable</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>prop1</td>" +
                    "<td>string</td>" +
                    "<td>true</td>" +
                    "<td>false</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>prop2</td>" +
                    "<td>string</td>" +
                    "<td>yes</td>" +
                    "<td>no</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>prop3</td>" +
                    "<td>string</td>" +
                    "<td>1</td>" +
                    "<td>0</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>prop4</td>" +
                    "<td>string</td>" +
                    "<td>required</td>" +
                    "<td>editable</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(4, properties.size());
        
        // Test various boolean formats
        assertTrue(properties.get(0).isRequired());
        assertFalse(properties.get(0).isWritable());
        
        assertTrue(properties.get(1).isRequired());
        assertFalse(properties.get(1).isWritable());
        
        assertTrue(properties.get(2).isRequired());
        assertFalse(properties.get(2).isWritable());
        
        assertTrue(properties.get(3).isRequired());
        assertFalse(properties.get(3).isWritable()); // "editable" doesn't match writable patterns
    }
    
    @Test
    void testExtractProperties_SkipMalformedRows() throws ExtractionException {
        String html = "<table>" +
                "<tr>" +
                    "<th>Property Name</th>" +
                    "<th>Type</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>validProp</td>" +
                    "<td>string</td>" +
                "</tr>" +
                "<tr>" +
                    "<td></td>" +
                    "<td>string</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>anotherValid</td>" +
                    "<td></td>" +
                "</tr>" +
                "<tr>" +
                    "<td>finalValid</td>" +
                    "<td>integer</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        // Should extract the valid rows (first and last)
        assertEquals(2, properties.size());
        assertEquals("validProp", properties.get(0).getName());
        assertEquals("finalValid", properties.get(1).getName());
    }
    
    @Test
    void testExtractProperties_NoHeaderRow() throws ExtractionException {
        String html = "<table>" +
                "<tr>" +
                    "<td>Property Name</td>" +
                    "<td>Type</td>" +
                    "<td>Required</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>testProp</td>" +
                    "<td>string</td>" +
                    "<td>true</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        assertEquals("testProp", properties.get(0).getName());
    }
    
    @Test
    void testExtractProperties_InvalidInput() {
        // Test with null input
        assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(null);
        });
        
        // Test with wrong type
        assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties("not an element");
        });
    }
    
    @Test
    void testExtractProperties_MissingRequiredColumns() {
        String html = "<table>" +
                "<tr>" +
                    "<th>Description</th>" +
                    "<th>Required</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>Some description</td>" +
                    "<td>true</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(table);
        });
    }
    
    @Test
    void testExtractProperties_EmptyTable() {
        String html = "<table>" +
                "<tr>" +
                    "<th>Property Name</th>" +
                    "<th>Type</th>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(table);
        });
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
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        assertThrows(ExtractionException.class, () -> {
            extractor.extractProperties(table);
        });
    }
    
    @Test
    void testExtractProperties_ComplexDescription() throws ExtractionException {
        String html = "<table>" +
                "<tr>" +
                    "<th>Property Name</th>" +
                    "<th>Type</th>" +
                    "<th>Description</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>complexProp</td>" +
                    "<td>string</td>" +
                    "<td>This is a   complex    description with\n" +
                        "multiple   spaces and line breaks</td>" +
                "</tr>" +
            "</table>";
        
        Document doc = Jsoup.parse(html);
        Element table = doc.select("table").first();
        
        List<PropertyDefinition> properties = extractor.extractProperties(table);
        
        assertEquals(1, properties.size());
        PropertyDefinition property = properties.get(0);
        assertEquals("complexProp", property.getName());
        assertEquals("This is a complex description with multiple spaces and line breaks", 
                     property.getDescription());
    }
}