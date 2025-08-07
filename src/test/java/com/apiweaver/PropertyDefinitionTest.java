package com.apiweaver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PropertyDefinition Tests")
class PropertyDefinitionTest {

    @Test
    @DisplayName("Should create valid PropertyDefinition with all parameters")
    void shouldCreateValidPropertyDefinition() {
        PropertyDefinition property = new PropertyDefinition(
            "testName", "string", true, false, "Test description"
        );

        assertEquals("testName", property.getName());
        assertEquals("string", property.getType());
        assertTrue(property.isRequired());
        assertFalse(property.isWritable());
        assertEquals("Test description", property.getDescription());
        assertTrue(property.isValid());
    }

    @Test
    @DisplayName("Should handle null description by setting empty string")
    void shouldHandleNullDescription() {
        PropertyDefinition property = new PropertyDefinition(
            "testName", "string", true, false, null
        );

        assertEquals("", property.getDescription());
        assertTrue(property.isValid());
    }

    @Test
    @DisplayName("Should throw NullPointerException for null name")
    void shouldThrowExceptionForNullName() {
        assertThrows(NullPointerException.class, () -> {
            new PropertyDefinition(null, "string", true, false, "description");
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException for null type")
    void shouldThrowExceptionForNullType() {
        assertThrows(NullPointerException.class, () -> {
            new PropertyDefinition("name", null, true, false, "description");
        });
    }

    @Test
    @DisplayName("Should be invalid with empty name")
    void shouldBeInvalidWithEmptyName() {
        PropertyDefinition property = new PropertyDefinition(
            "", "string", true, false, "description"
        );

        assertFalse(property.isValid());
    }

    @Test
    @DisplayName("Should be invalid with whitespace-only name")
    void shouldBeInvalidWithWhitespaceOnlyName() {
        PropertyDefinition property = new PropertyDefinition(
            "   ", "string", true, false, "description"
        );

        assertFalse(property.isValid());
    }

    @Test
    @DisplayName("Should be invalid with empty type")
    void shouldBeInvalidWithEmptyType() {
        PropertyDefinition property = new PropertyDefinition(
            "name", "", true, false, "description"
        );

        assertFalse(property.isValid());
    }

    @Test
    @DisplayName("Should be invalid with whitespace-only type")
    void shouldBeInvalidWithWhitespaceOnlyType() {
        PropertyDefinition property = new PropertyDefinition(
            "name", "   ", true, false, "description"
        );

        assertFalse(property.isValid());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        PropertyDefinition property1 = new PropertyDefinition(
            "name", "string", true, false, "description"
        );
        PropertyDefinition property2 = new PropertyDefinition(
            "name", "string", true, false, "description"
        );
        PropertyDefinition property3 = new PropertyDefinition(
            "different", "string", true, false, "description"
        );

        assertEquals(property1, property2);
        assertNotEquals(property1, property3);
        assertNotEquals(property1, null);
        assertNotEquals(property1, "not a PropertyDefinition");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        PropertyDefinition property1 = new PropertyDefinition(
            "name", "string", true, false, "description"
        );
        PropertyDefinition property2 = new PropertyDefinition(
            "name", "string", true, false, "description"
        );

        assertEquals(property1.hashCode(), property2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        PropertyDefinition property = new PropertyDefinition(
            "testName", "string", true, false, "Test description"
        );

        String toString = property.toString();
        assertTrue(toString.contains("testName"));
        assertTrue(toString.contains("string"));
        assertTrue(toString.contains("true"));
        assertTrue(toString.contains("false"));
        assertTrue(toString.contains("Test description"));
    }
}