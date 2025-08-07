package com.apiweaver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TimeTapPropertyMapper.
 */
class TimeTapPropertyMapperTest {
    
    private TimeTapPropertyMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new TimeTapPropertyMapper();
    }
    
    @Nested
    @DisplayName("HTML Type to OpenAPI Type Mapping")
    class HtmlTypeToOpenApiTypeMapping {
        
        @Test
        @DisplayName("Should map string types correctly")
        void shouldMapStringTypes() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("string"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("String"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("text"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("Text"));
        }
        
        @Test
        @DisplayName("Should map integer types correctly")
        void shouldMapIntegerTypes() {
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("integer"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("Integer"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("int"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("Int"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("long"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("Long"));
        }
        
        @Test
        @DisplayName("Should map number types correctly")
        void shouldMapNumberTypes() {
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("number"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("Number"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("decimal"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("Decimal"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("float"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("Float"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("double"));
            assertEquals("number", mapper.mapHtmlTypeToOpenApiType("Double"));
        }
        
        @Test
        @DisplayName("Should map boolean types correctly")
        void shouldMapBooleanTypes() {
            assertEquals("boolean", mapper.mapHtmlTypeToOpenApiType("boolean"));
            assertEquals("boolean", mapper.mapHtmlTypeToOpenApiType("Boolean"));
            assertEquals("boolean", mapper.mapHtmlTypeToOpenApiType("bool"));
            assertEquals("boolean", mapper.mapHtmlTypeToOpenApiType("Bool"));
        }
        
        @Test
        @DisplayName("Should map date/time types correctly")
        void shouldMapDateTimeTypes() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("date"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("Date"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("datetime"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("DateTime"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("timestamp"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("Timestamp"));
        }
        
        @Test
        @DisplayName("Should map array types correctly")
        void shouldMapArrayTypes() {
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("array"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("Array"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("list"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("List"));
        }
        
        @Test
        @DisplayName("Should map object types correctly")
        void shouldMapObjectTypes() {
            assertEquals("object", mapper.mapHtmlTypeToOpenApiType("object"));
            assertEquals("object", mapper.mapHtmlTypeToOpenApiType("Object"));
        }
        
        @Test
        @DisplayName("Should map TimeTap specific types correctly")
        void shouldMapTimeTapSpecificTypes() {
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("id"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("ID"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("uuid"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("UUID"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("email"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("Email"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("url"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("URL"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("uri"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("URI"));
        }
        
        @Test
        @DisplayName("Should handle array notation correctly")
        void shouldHandleArrayNotation() {
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("string[]"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("Integer[]"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("Array[String]"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("List[Integer]"));
        }
        
        @Test
        @DisplayName("Should handle nullable types correctly")
        void shouldHandleNullableTypes() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("string?"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("Integer?"));
            assertEquals("boolean", mapper.mapHtmlTypeToOpenApiType("Boolean?"));
        }
        
        @Test
        @DisplayName("Should handle case-insensitive fallback")
        void shouldHandleCaseInsensitiveFallback() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("STRING_VALUE"));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("integer_id"));
            assertEquals("boolean", mapper.mapHtmlTypeToOpenApiType("BOOLEAN_FLAG"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("datetime_field"));
        }
        
        @Test
        @DisplayName("Should return string as default for unknown types")
        void shouldReturnStringAsDefaultForUnknownTypes() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("unknown_type"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("custom_type"));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("weird123"));
        }
        
        @Test
        @DisplayName("Should handle null and empty input")
        void shouldHandleNullAndEmptyInput() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType(null));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType(""));
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("   "));
        }
    }
    
    @Nested
    @DisplayName("Property Definition to OpenAPI Property Mapping")
    class PropertyDefinitionToOpenApiPropertyMapping {
        
        @Test
        @DisplayName("Should map basic property correctly")
        void shouldMapBasicPropertyCorrectly() {
            PropertyDefinition property = new PropertyDefinition(
                "userName", "string", true, true, "The user's name"
            );
            
            OpenApiProperty result = mapper.mapToOpenApiProperty(property);
            
            assertEquals("userName", result.getName());
            assertEquals("string", result.getType());
            assertNull(result.getFormat());
            assertTrue(result.isRequired());
            assertFalse(result.isReadOnly());
            assertEquals("The user's name", result.getDescription());
        }
        
        @Test
        @DisplayName("Should map property with format correctly")
        void shouldMapPropertyWithFormatCorrectly() {
            PropertyDefinition property = new PropertyDefinition(
                "createdAt", "datetime", false, false, "Creation timestamp"
            );
            
            OpenApiProperty result = mapper.mapToOpenApiProperty(property);
            
            assertEquals("createdAt", result.getName());
            assertEquals("string", result.getType());
            assertEquals("date-time", result.getFormat());
            assertFalse(result.isRequired());
            assertTrue(result.isReadOnly()); // not writable = readOnly
            assertEquals("Creation timestamp", result.getDescription());
        }
        
        @Test
        @DisplayName("Should handle required and writable flags correctly")
        void shouldHandleRequiredAndWritableFlags() {
            // Required and writable
            PropertyDefinition property1 = new PropertyDefinition(
                "name", "string", true, true, "Name field"
            );
            OpenApiProperty result1 = mapper.mapToOpenApiProperty(property1);
            assertTrue(result1.isRequired());
            assertFalse(result1.isReadOnly());
            
            // Required but not writable (read-only)
            PropertyDefinition property2 = new PropertyDefinition(
                "id", "integer", true, false, "ID field"
            );
            OpenApiProperty result2 = mapper.mapToOpenApiProperty(property2);
            assertTrue(result2.isRequired());
            assertTrue(result2.isReadOnly());
            
            // Not required but writable
            PropertyDefinition property3 = new PropertyDefinition(
                "optional", "string", false, true, "Optional field"
            );
            OpenApiProperty result3 = mapper.mapToOpenApiProperty(property3);
            assertFalse(result3.isRequired());
            assertFalse(result3.isReadOnly());
            
            // Not required and not writable
            PropertyDefinition property4 = new PropertyDefinition(
                "computed", "string", false, false, "Computed field"
            );
            OpenApiProperty result4 = mapper.mapToOpenApiProperty(property4);
            assertFalse(result4.isRequired());
            assertTrue(result4.isReadOnly());
        }
        
        @Test
        @DisplayName("Should handle different type mappings in full property context")
        void shouldHandleDifferentTypeMappingsInFullPropertyContext() {
            // Integer with format
            PropertyDefinition intProperty = new PropertyDefinition(
                "userId", "id", true, false, "User identifier"
            );
            OpenApiProperty intResult = mapper.mapToOpenApiProperty(intProperty);
            assertEquals("integer", intResult.getType());
            assertEquals("int64", intResult.getFormat());
            
            // String with format
            PropertyDefinition emailProperty = new PropertyDefinition(
                "userEmail", "email", true, true, "User email address"
            );
            OpenApiProperty emailResult = mapper.mapToOpenApiProperty(emailProperty);
            assertEquals("string", emailResult.getType());
            assertEquals("email", emailResult.getFormat());
            
            // Array type
            PropertyDefinition arrayProperty = new PropertyDefinition(
                "tags", "string[]", false, true, "List of tags"
            );
            OpenApiProperty arrayResult = mapper.mapToOpenApiProperty(arrayProperty);
            assertEquals("array", arrayResult.getType());
            assertNull(arrayResult.getFormat());
        }
        
        @Test
        @DisplayName("Should handle empty description")
        void shouldHandleEmptyDescription() {
            PropertyDefinition property = new PropertyDefinition(
                "field", "string", false, true, null
            );
            
            OpenApiProperty result = mapper.mapToOpenApiProperty(property);
            
            assertEquals("", result.getDescription());
        }
        
        @Test
        @DisplayName("Should throw exception for null property definition")
        void shouldThrowExceptionForNullPropertyDefinition() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.mapToOpenApiProperty(null)
            );
            assertEquals("Property definition cannot be null", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid property definition")
        void shouldThrowExceptionForInvalidPropertyDefinition() {
            PropertyDefinition invalidProperty = new PropertyDefinition(
                "", "string", false, true, "Invalid property"
            );
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.mapToOpenApiProperty(invalidProperty)
            );
            assertTrue(exception.getMessage().contains("Property definition is not valid"));
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {
        
        @Test
        @DisplayName("Should handle whitespace in type names")
        void shouldHandleWhitespaceInTypeNames() {
            assertEquals("string", mapper.mapHtmlTypeToOpenApiType("  string  "));
            assertEquals("integer", mapper.mapHtmlTypeToOpenApiType("\tinteger\n"));
        }
        
        @Test
        @DisplayName("Should handle complex type expressions")
        void shouldHandleComplexTypeExpressions() {
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("Array[String]"));
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("List[Integer]"));
            // Optional types with brackets should be treated as arrays by current logic
            assertEquals("array", mapper.mapHtmlTypeToOpenApiType("Optional[String]"));
        }
        
        @Test
        @DisplayName("Should be consistent with repeated calls")
        void shouldBeConsistentWithRepeatedCalls() {
            String type = "custom_unknown_type";
            String result1 = mapper.mapHtmlTypeToOpenApiType(type);
            String result2 = mapper.mapHtmlTypeToOpenApiType(type);
            String result3 = mapper.mapHtmlTypeToOpenApiType(type);
            
            assertEquals(result1, result2);
            assertEquals(result2, result3);
        }
    }
}