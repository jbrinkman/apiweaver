package com.apiweaver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Configuration Tests")
class ConfigurationTest {

    @Test
    @DisplayName("Should create Configuration with builder defaults")
    void shouldCreateConfigurationWithBuilderDefaults() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .build();

        assertEquals("https://example.com", config.getUrl());
        assertEquals("generated-api.yaml", config.getOutputFile());
        assertNull(config.getExistingSpecFile());
        assertFalse(config.isVerbose());
        assertEquals(30000, config.getTimeoutMs());
        assertTrue(config.isValid());
    }

    @Test
    @DisplayName("Should create Configuration with all parameters")
    void shouldCreateConfigurationWithAllParameters() {
        Configuration config = Configuration.builder()
            .url("https://api.example.com")
            .outputFile("custom-output.yaml")
            .existingSpecFile("existing.yaml")
            .verbose(true)
            .timeoutMs(60000)
            .build();

        assertEquals("https://api.example.com", config.getUrl());
        assertEquals("custom-output.yaml", config.getOutputFile());
        assertEquals("existing.yaml", config.getExistingSpecFile());
        assertTrue(config.isVerbose());
        assertEquals(60000, config.getTimeoutMs());
        assertTrue(config.isValid());
    }

    @Test
    @DisplayName("Should support method chaining in builder")
    void shouldSupportMethodChainingInBuilder() {
        Configuration config = Configuration.builder()
            .url("https://test.com")
            .outputFile("test.yaml")
            .verbose(true)
            .timeoutMs(45000)
            .existingSpecFile("test-existing.yaml")
            .build();

        assertEquals("https://test.com", config.getUrl());
        assertEquals("test.yaml", config.getOutputFile());
        assertEquals("test-existing.yaml", config.getExistingSpecFile());
        assertTrue(config.isVerbose());
        assertEquals(45000, config.getTimeoutMs());
    }

    @Test
    @DisplayName("Should be invalid with null URL")
    void shouldBeInvalidWithNullUrl() {
        Configuration config = Configuration.builder()
            .url(null)
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with empty URL")
    void shouldBeInvalidWithEmptyUrl() {
        Configuration config = Configuration.builder()
            .url("")
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with whitespace-only URL")
    void shouldBeInvalidWithWhitespaceOnlyUrl() {
        Configuration config = Configuration.builder()
            .url("   ")
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with null output file")
    void shouldBeInvalidWithNullOutputFile() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .outputFile(null)
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with empty output file")
    void shouldBeInvalidWithEmptyOutputFile() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .outputFile("")
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with whitespace-only output file")
    void shouldBeInvalidWithWhitespaceOnlyOutputFile() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .outputFile("   ")
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with zero timeout")
    void shouldBeInvalidWithZeroTimeout() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .timeoutMs(0)
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be invalid with negative timeout")
    void shouldBeInvalidWithNegativeTimeout() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .timeoutMs(-1000)
            .build();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("Should be valid with minimum positive timeout")
    void shouldBeValidWithMinimumPositiveTimeout() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .timeoutMs(1)
            .build();

        assertTrue(config.isValid());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Configuration config1 = Configuration.builder()
            .url("https://example.com")
            .outputFile("test.yaml")
            .verbose(true)
            .timeoutMs(30000)
            .build();

        Configuration config2 = Configuration.builder()
            .url("https://example.com")
            .outputFile("test.yaml")
            .verbose(true)
            .timeoutMs(30000)
            .build();

        Configuration config3 = Configuration.builder()
            .url("https://different.com")
            .outputFile("test.yaml")
            .verbose(true)
            .timeoutMs(30000)
            .build();

        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertNotEquals(config1, null);
        assertNotEquals(config1, "not a Configuration");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        Configuration config1 = Configuration.builder()
            .url("https://example.com")
            .outputFile("test.yaml")
            .verbose(true)
            .timeoutMs(30000)
            .build();

        Configuration config2 = Configuration.builder()
            .url("https://example.com")
            .outputFile("test.yaml")
            .verbose(true)
            .timeoutMs(30000)
            .build();

        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        Configuration config = Configuration.builder()
            .url("https://example.com")
            .outputFile("test.yaml")
            .existingSpecFile("existing.yaml")
            .verbose(true)
            .timeoutMs(45000)
            .build();

        String toString = config.toString();
        assertTrue(toString.contains("https://example.com"));
        assertTrue(toString.contains("test.yaml"));
        assertTrue(toString.contains("existing.yaml"));
        assertTrue(toString.contains("true"));
        assertTrue(toString.contains("45000"));
    }
}