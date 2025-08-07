package com.apiweaver;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JSoupHtmlParser class.
 * Tests HTML parsing functionality and element extraction methods.
 */
class JSoupHtmlParserTest {
    
    private JSoupHtmlParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new JSoupHtmlParser();
    }
    
    @Nested
    @DisplayName("parseHtml method tests")
    class ParseHtmlTests {
        
        @Test
        @DisplayName("Should parse valid HTML content successfully")
        void shouldParseValidHtml() {
            String htmlContent = "<html><body><h1>Test</h1><p>Content</p></body></html>";
            
            Document doc = parser.parseHtml(htmlContent);
            
            assertNotNull(doc);
            assertEquals("Test", doc.select("h1").first().text());
            assertEquals("Content", doc.select("p").first().text());
        }
        
        @Test
        @DisplayName("Should parse HTML fragment without html/body tags")
        void shouldParseHtmlFragment() {
            String htmlContent = "<h2 id=\"testObjectValues\">Test Header</h2><table><tr><td>Cell</td></tr></table>";
            
            Document doc = parser.parseHtml(htmlContent);
            
            assertNotNull(doc);
            assertEquals("Test Header", doc.select("h2").first().text());
            assertEquals("testObjectValues", doc.select("h2").first().attr("id"));
        }
        
        @Test
        @DisplayName("Should throw exception for null HTML content")
        void shouldThrowExceptionForNullHtml() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseHtml(null)
            );
            assertEquals("HTML content cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for empty HTML content")
        void shouldThrowExceptionForEmptyHtml() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseHtml("")
            );
            assertEquals("HTML content cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for whitespace-only HTML content")
        void shouldThrowExceptionForWhitespaceOnlyHtml() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parseHtml("   \n\t  ")
            );
            assertEquals("HTML content cannot be null or empty", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("findH2ElementsWithIdEndingIn method tests")
    class FindH2ElementsTests {
        
        @Test
        @DisplayName("Should find H2 elements with matching id suffix")
        void shouldFindH2ElementsWithMatchingSuffix() {
            String htmlContent = "<html>" +
                "<body>" +
                "<h2 id=\"userObjectValues\">User Properties</h2>" +
                "<h2 id=\"documentObjectValues\">Document Properties</h2>" +
                "<h2 id=\"otherSection\">Other Section</h2>" +
                "<h2 id=\"projectObjectValues\">Project Properties</h2>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            List<Element> elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            
            assertEquals(3, elements.size());
            assertEquals("userObjectValues", elements.get(0).attr("id"));
            assertEquals("documentObjectValues", elements.get(1).attr("id"));
            assertEquals("projectObjectValues", elements.get(2).attr("id"));
        }
        
        @Test
        @DisplayName("Should return empty list when no matching H2 elements found")
        void shouldReturnEmptyListWhenNoMatchingElements() {
            String htmlContent = "<html>" +
                "<body>" +
                "<h2 id=\"section1\">Section 1</h2>" +
                "<h2 id=\"section2\">Section 2</h2>" +
                "<h1 id=\"titleObjectValues\">Title</h1>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            List<Element> elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            
            assertTrue(elements.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle H2 elements without id attributes")
        void shouldHandleH2ElementsWithoutId() {
            String htmlContent = "<html>" +
                "<body>" +
                "<h2>Header without ID</h2>" +
                "<h2 id=\"testObjectValues\">Header with ID</h2>" +
                "<h2>Another header without ID</h2>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            List<Element> elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            
            assertEquals(1, elements.size());
            assertEquals("testObjectValues", elements.get(0).attr("id"));
        }
        
        @Test
        @DisplayName("Should be case sensitive when matching suffix")
        void shouldBeCaseSensitiveWhenMatchingSuffix() {
            String htmlContent = "<html>" +
                "<body>" +
                "<h2 id=\"testObjectValues\">Correct Case</h2>" +
                "<h2 id=\"testobjectvalues\">Wrong Case</h2>" +
                "<h2 id=\"testOBJECTVALUES\">Wrong Case</h2>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            List<Element> elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            
            assertEquals(1, elements.size());
            assertEquals("testObjectValues", elements.get(0).attr("id"));
        }
        
        @Test
        @DisplayName("Should throw exception for null document")
        void shouldThrowExceptionForNullDocument() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.findH2ElementsWithIdEndingIn(null, "ObjectValues")
            );
            assertEquals("Document cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for null suffix")
        void shouldThrowExceptionForNullSuffix() {
            Document doc = parser.parseHtml("<html><body></body></html>");
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.findH2ElementsWithIdEndingIn(doc, null)
            );
            assertEquals("Suffix cannot be null or empty", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for empty suffix")
        void shouldThrowExceptionForEmptySuffix() {
            Document doc = parser.parseHtml("<html><body></body></html>");
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.findH2ElementsWithIdEndingIn(doc, "")
            );
            assertEquals("Suffix cannot be null or empty", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("findFirstTableAfterElement method tests")
    class FindFirstTableAfterElementTests {
        
        @Test
        @DisplayName("Should find first table after specified H2 element")
        void shouldFindFirstTableAfterH2Element() {
            String htmlContent = "<html>" +
                "<body>" +
                "<h2 id=\"userObjectValues\">User Properties</h2>" +
                "<p>Some description text</p>" +
                "<div>Some other content</div>" +
                "<table id=\"userTable\">" +
                "<tr><th>Property</th><th>Type</th></tr>" +
                "<tr><td>name</td><td>string</td></tr>" +
                "</table>" +
                "<table id=\"anotherTable\">" +
                "<tr><th>Other</th></tr>" +
                "</table>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            Element h2Element = doc.select("h2#userObjectValues").first();
            Element table = parser.findFirstTableAfterElement(doc, h2Element);
            
            assertNotNull(table);
            assertEquals("userTable", table.attr("id"));
            assertEquals("table", table.tagName());
        }
        
        @Test
        @DisplayName("Should return null when no table found after element")
        void shouldReturnNullWhenNoTableFound() {
            String htmlContent = "<html>" +
                "<body>" +
                "<table id=\"beforeTable\">" +
                "<tr><td>Before</td></tr>" +
                "</table>" +
                "<h2 id=\"testHeader\">Test Header</h2>" +
                "<p>Some text</p>" +
                "<div>Some content</div>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            Element h2Element = doc.select("h2#testHeader").first();
            Element table = parser.findFirstTableAfterElement(doc, h2Element);
            
            assertNull(table);
        }
        
        @Test
        @DisplayName("Should find table even when nested in other elements")
        void shouldFindNestedTable() {
            String htmlContent = "<html>" +
                "<body>" +
                "<h2 id=\"testHeader\">Test Header</h2>" +
                "<div class=\"content\">" +
                "<div class=\"inner\">" +
                "<table id=\"nestedTable\">" +
                "<tr><td>Nested</td></tr>" +
                "</table>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            Element h2Element = doc.select("h2#testHeader").first();
            Element table = parser.findFirstTableAfterElement(doc, h2Element);
            
            assertNotNull(table);
            assertEquals("nestedTable", table.attr("id"));
        }
        
        @Test
        @DisplayName("Should skip tables that appear before the specified element")
        void shouldSkipTablesBeforeElement() {
            String htmlContent = "<html>" +
                "<body>" +
                "<table id=\"beforeTable\">" +
                "<tr><td>Before</td></tr>" +
                "</table>" +
                "<h2 id=\"testHeader\">Test Header</h2>" +
                "<table id=\"afterTable\">" +
                "<tr><td>After</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            Element h2Element = doc.select("h2#testHeader").first();
            Element table = parser.findFirstTableAfterElement(doc, h2Element);
            
            assertNotNull(table);
            assertEquals("afterTable", table.attr("id"));
        }
        
        @Test
        @DisplayName("Should throw exception for null document")
        void shouldThrowExceptionForNullDocument() {
            Element element = parser.parseHtml("<h2>Test</h2>").select("h2").first();
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.findFirstTableAfterElement(null, element)
            );
            assertEquals("Document cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for null element")
        void shouldThrowExceptionForNullElement() {
            Document doc = parser.parseHtml("<html><body></body></html>");
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.findFirstTableAfterElement(doc, null)
            );
            assertEquals("Element cannot be null", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete TimeTap-like HTML structure")
        void shouldHandleCompleteTimeTapLikeStructure() {
            String htmlContent = "<html>" +
                "<head><title>TimeTap API Documentation</title></head>" +
                "<body>" +
                "<h1>API Documentation</h1>" +
                "<h2 id=\"introduction\">Introduction</h2>" +
                "<p>Welcome to the API documentation</p>" +
                "<h2 id=\"userObjectValues\">User Object Values</h2>" +
                "<p>The following table describes the properties of the User object:</p>" +
                "<table class=\"properties\">" +
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
                "<td>Yes</td>" +
                "<td>No</td>" +
                "<td>Unique identifier for the user</td>" +
                "</tr>" +
                "<tr>" +
                "<td>name</td>" +
                "<td>string</td>" +
                "<td>Yes</td>" +
                "<td>Yes</td>" +
                "<td>Full name of the user</td>" +
                "</tr>" +
                "</tbody>" +
                "</table>" +
                "<h2 id=\"projectObjectValues\">Project Object Values</h2>" +
                "<p>The following table describes the properties of the Project object:</p>" +
                "<table class=\"properties\">" +
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
                "<td>projectId</td>" +
                "<td>integer</td>" +
                "<td>Yes</td>" +
                "<td>No</td>" +
                "<td>Unique identifier for the project</td>" +
                "</tr>" +
                "</tbody>" +
                "</table>" +
                "</body>" +
                "</html>";
            
            Document doc = parser.parseHtml(htmlContent);
            
            // Test finding H2 elements with ObjectValues suffix
            List<Element> h2Elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            assertEquals(2, h2Elements.size());
            assertEquals("userObjectValues", h2Elements.get(0).attr("id"));
            assertEquals("projectObjectValues", h2Elements.get(1).attr("id"));
            
            // Test finding table after first H2 element
            Element userTable = parser.findFirstTableAfterElement(doc, h2Elements.get(0));
            assertNotNull(userTable);
            assertEquals("properties", userTable.attr("class"));
            
            // Verify table content
            assertEquals(3, userTable.select("tr").size()); // header + 2 data rows
            assertEquals("Property Name", userTable.select("th").first().text());
            assertEquals("id", userTable.select("tbody tr").first().select("td").first().text());
            
            // Test finding table after second H2 element
            Element projectTable = parser.findFirstTableAfterElement(doc, h2Elements.get(1));
            assertNotNull(projectTable);
            assertEquals(2, projectTable.select("tr").size()); // header + 1 data row
            assertEquals("projectId", projectTable.select("tbody tr").first().select("td").first().text());
        }
    }
    
    @Nested
    @DisplayName("Real-world HTML sample tests")
    class RealWorldHtmlTests {
        
        private String loadResourceFile(String filename) throws IOException {
            Path resourcePath = Paths.get("src", "test", "resources", filename);
            return Files.readString(resourcePath);
        }
        
        @Test
        @DisplayName("Should parse and extract from TimeTap Resource HTML")
        void shouldParseTimeTapResourceHtml() throws IOException {
            // Load the sample HTML file
            String htmlContent = loadResourceFile("sample-timetap-resource.html");
            Document doc = parser.parseHtml(htmlContent);
            
            // Test finding H2 elements with ObjectValues suffix
            List<Element> h2Elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            assertEquals(1, h2Elements.size(), "Should find exactly one H2 element with ObjectValues suffix");
            assertEquals("ResourceObjectValues", h2Elements.get(0).attr("id"));
            assertEquals("Resource Object Values", h2Elements.get(0).text());
            
            // Test finding the table after the H2 element
            Element h2Element = h2Elements.get(0);
            Element table = parser.findFirstTableAfterElement(doc, h2Element);
            assertNotNull(table, "Should find a table after the H2 element");
            assertEquals("confluenceTable", table.className());
            
            // Verify table structure
            Elements rows = table.select("tr");
            assertTrue(rows.size() > 1, "Table should have multiple rows");
            
            // Verify header row
            Element headerRow = rows.first();
            Elements headerCells = headerRow.select("th");
            assertEquals(3, headerCells.size(), "Header row should have 3 cells");
            assertEquals("Property", headerCells.get(0).text());
            assertEquals("Type", headerCells.get(1).text());
            assertEquals("Description", headerCells.get(2).text());
            
            // Verify a data row
            Element dataRow = rows.get(1);
            Elements dataCells = dataRow.select("td");
            assertEquals(3, dataCells.size(), "Data row should have 3 cells");
            assertEquals("active", dataCells.get(0).text());
            assertEquals("boolean", dataCells.get(1).text());
        }
        
        @Test
        @DisplayName("Should handle multiple H2 elements with ObjectValues suffix")
        void shouldHandleMultipleObjectValuesSections() throws IOException {
            // Load the sample HTML file with multiple ObjectValues sections
            String htmlContent = loadResourceFile("multiple-objectvalues.html");
            Document doc = parser.parseHtml(htmlContent);
            
            // Test finding all H2 elements with ObjectValues suffix
            List<Element> h2Elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            assertEquals(3, h2Elements.size(), "Should find exactly three H2 elements with ObjectValues suffix");
            
            // Verify the IDs of the found elements
            assertEquals("FirstObjectValues", h2Elements.get(0).attr("id"));
            assertEquals("SecondObjectValues", h2Elements.get(1).attr("id"));
            assertEquals("ThirdObjectValues", h2Elements.get(2).attr("id"));
            
            // Test finding tables after each H2 element
            for (Element h2Element : h2Elements) {
                Element table = parser.findFirstTableAfterElement(doc, h2Element);
                assertNotNull(table, "Should find a table after each H2 element");
                assertEquals("confluenceTable", table.className());
                
                // Verify the property in the first data row matches the expected pattern
                Element dataRow = table.select("tr").get(1);
                String propertyName = dataRow.select("td").first().text();
                
                if (h2Element.attr("id").equals("FirstObjectValues")) {
                    assertEquals("firstProperty", propertyName);
                } else if (h2Element.attr("id").equals("SecondObjectValues")) {
                    assertEquals("secondProperty", propertyName);
                } else if (h2Element.attr("id").equals("ThirdObjectValues")) {
                    assertEquals("thirdProperty", propertyName);
                }
            }
        }
        
        @Test
        @DisplayName("Should handle HTML with no ObjectValues sections")
        void shouldHandleNoObjectValuesSections() throws IOException {
            // Load the sample HTML file with no ObjectValues sections
            String htmlContent = loadResourceFile("no-objectvalues.html");
            Document doc = parser.parseHtml(htmlContent);
            
            // Test finding H2 elements with ObjectValues suffix
            List<Element> h2Elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            assertTrue(h2Elements.isEmpty(), "Should not find any H2 elements with ObjectValues suffix");
            
            // Verify other H2 elements exist but weren't matched
            Elements allH2Elements = doc.select("h2");
            assertFalse(allH2Elements.isEmpty(), "Document should contain H2 elements");
            assertEquals(4, allH2Elements.size(), "Document should contain 4 H2 elements");
        }
        
        @Test
        @DisplayName("Should handle malformed HTML tables gracefully")
        void shouldHandleMalformedTables() throws IOException {
            // Load the sample HTML file with malformed tables
            String htmlContent = loadResourceFile("malformed-table.html");
            Document doc = parser.parseHtml(htmlContent);
            
            // Test finding H2 elements with ObjectValues suffix
            List<Element> h2Elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            assertEquals(1, h2Elements.size(), "Should find exactly one H2 element with ObjectValues suffix");
            
            // Test finding the table after the H2 element
            Element h2Element = h2Elements.get(0);
            Element table = parser.findFirstTableAfterElement(doc, h2Element);
            assertNotNull(table, "Should find a table after the H2 element even if malformed");
            
            // JSoup should normalize the HTML, so we can still access rows
            Elements rows = table.select("tr");
            assertTrue(rows.size() > 1, "Table should have multiple rows");
            
            // Check that we can access the first valid row without exceptions
            Element firstValidRow = rows.get(1);
            Elements cells = firstValidRow.select("td");
            assertEquals(3, cells.size(), "First valid row should have 3 cells");
            assertEquals("validProperty", cells.get(0).text());
            
            // Check a row with missing cells
            Element rowWithMissingCell = rows.get(2);
            Elements missingCells = rowWithMissingCell.select("td");
            assertEquals(2, missingCells.size(), "Row with missing cell should have 2 cells");
            assertEquals("missingTypeCell", missingCells.get(0).text());
            
            // Check a row with extra cells
            Element rowWithExtraCell = rows.get(3);
            Elements extraCells = rowWithExtraCell.select("td");
            assertEquals(4, extraCells.size(), "Row with extra cell should have 4 cells");
            assertEquals("extraCell", extraCells.get(0).text());
        }
    }
}