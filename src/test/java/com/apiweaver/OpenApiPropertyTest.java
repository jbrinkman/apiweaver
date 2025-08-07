package com.apiweaver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OpenApiProperty Tests")
class OpenApiPropertyTest {

    @Test
    @DisplayName("Should create OpenApiProperty with all parameters")
    void shouldCreateOpenApiPropertyWithAllParameters() {
        OpenApiProperty property = new OpenApiProperty(
            "testName", "string", "email", true, false, "Test description"
        );

        assertEquals("testName", property.getName());
        assertEquals("string", property.getType());
        assertEquals("email", property.getFormat());
        assertTrue(property.isRequired());
        assertFalse(property.isReadOnly());
        assertEquals("Test description", property.getDescription());
    }

    @Test
    @DisplayName("Should handle null format")
    void shouldHandleNullFormat() {
        OpenApiProperty property = new OpenApiProperty(
            "testName", "string", null, true, false, "Test description"
        );

        assertNull(property.getFormat());
    }

    @Test
    @DisplayName("Should handle null description by setting empty string")
    void shouldHandleNullDescription() {
        OpenApiProperty property = new OpenApiProperty(
            "testName", "string", "email", true, false, null
        );

        assertEquals("", property.getDescription());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null name")
    void shouldThrowExceptionForNullName() {
        assertThrows(NullPointerException.class, () -> {
            new OpenApiProperty(null, "string", "email", true, false, "description");
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException for null type")
    void shouldThrowExceptionForNullType() {
        assertThrows(NullPointerException.class, () -> {
            new OpenApiProperty("name", null, "email", true, false, "description");
        });
    }

    @Test
    @DisplayName("Should create builder with required parameters")
    void shouldCreateBuilderWithRequiredParameters() {
        OpenApiProperty.Builder builder = OpenApiProperty.builder("testName", "string");
        assertNotNull(builder);

        OpenApiProperty property = builder.build();
        assertEquals("testName", property.getName());
        assertEquals("string", property.getType());
        assertNull(property.getFormat());
        assertFalse(property.isRequired());
        assertFalse(property.isReadOnly());
        assertEquals("", property.getDescription());
    }

    @Test
    @DisplayName("Should build property with all optional parameters")
    void shouldBuildPropertyWithAllOptionalParameters() {
        OpenApiProperty property = OpenApiProperty.builder("testName", "string")
            .format("email")
            .required(true)
            .readOnly(true)
            .description("Test description")
            .build();

        assertEquals("testName", property.getName());
        assertEquals("string", property.getType());
        assertEquals("email", property.getFormat());
        assertTrue(property.isRequired());
        assertTrue(property.isReadOnly());
        assertEquals("Test description", property.getDescription());
    }

    @Test
    @DisplayName("Should support method chaining in builder")
    void shouldSupportMethodChainingInBuilder() {
        OpenApiProperty property = OpenApiProperty.builder("testName", "string")
            .format("date-time")
            .required(true)
            .readOnly(false)
            .description("Chained description")
            .build();

        assertEquals("date-time", property.getFormat());
        assertTrue(property.isRequired());
        assertFalse(property.isReadOnly());
        assertEquals("Chained description", property.getDescription());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        OpenApiProperty property1 = new OpenApiProperty(
            "name", "string", "email", true, false, "description"
        );
        OpenApiProperty property2 = new OpenApiProperty(
            "name", "string", "email", true, false, "description"
        );
        OpenApiProperty property3 = new OpenApiProperty(
            "different", "string", "email", true, false, "description"
        );

        assertEquals(property1, property2);
        assertNotEquals(property1, property3);
        assertNotEquals(property1, null);
        assertNotEquals(property1, "not an OpenApiProperty");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        OpenApiProperty property1 = new OpenApiProperty(
            "name", "string", "email", true, false, "description"
        );
        OpenApiProperty property2 = new OpenApiProperty(
            "name", "string", "email", true, false, "description"
        );

        assertEquals(property1.hashCode(), property2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        OpenApiProperty property = new OpenApiProperty(
            "testName", "string", "email", true, false, "Test description"
        );

        String toString = property.toString();
        assertTrue(toString.contains("testName"));
        assertTrue(toString.contains("string"));
        assertTrue(toString.contains("email"));
        assertTrue(toString.contains("true"));
        assertTrue(toString.contains("false"));
        assertTrue(toString.contains("Test description"));
    }
}