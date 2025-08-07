package com.apiweaver;

import java.util.Objects;

/**
 * Represents an OpenAPI property with all necessary attributes for schema generation.
 * This is the target format after mapping from HTML property definitions.
 */
public class OpenApiProperty {
    private final String name;
    private final String type;
    private final String format;
    private final boolean required;
    private final boolean readOnly;
    private final String description;

    /**
     * Creates a new OpenAPI property.
     * 
     * @param name the property name
     * @param type the OpenAPI type (string, integer, boolean, etc.)
     * @param format the OpenAPI format (date-time, email, etc.), can be null
     * @param required whether the property is required
     * @param readOnly whether the property is read-only
     * @param description the property description
     */
    public OpenApiProperty(String name, String type, String format, boolean required, boolean readOnly, String description) {
        this.name = Objects.requireNonNull(name, "Property name cannot be null");
        this.type = Objects.requireNonNull(type, "Property type cannot be null");
        this.format = format;
        this.required = required;
        this.readOnly = readOnly;
        this.description = description != null ? description : "";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Creates a builder for constructing OpenAPI properties.
     * 
     * @param name the property name
     * @param type the property type
     * @return a new builder instance
     */
    public static Builder builder(String name, String type) {
        return new Builder(name, type);
    }

    /**
     * Builder class for creating OpenAPI properties with optional attributes.
     */
    public static class Builder {
        private final String name;
        private final String type;
        private String format;
        private boolean required = false;
        private boolean readOnly = false;
        private String description = "";

        private Builder(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public OpenApiProperty build() {
            return new OpenApiProperty(name, type, format, required, readOnly, description);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenApiProperty that = (OpenApiProperty) o;
        return required == that.required &&
               readOnly == that.readOnly &&
               Objects.equals(name, that.name) &&
               Objects.equals(type, that.type) &&
               Objects.equals(format, that.format) &&
               Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, format, required, readOnly, description);
    }

    @Override
    public String toString() {
        return "OpenApiProperty{" +
               "name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", format='" + format + '\'' +
               ", required=" + required +
               ", readOnly=" + readOnly +
               ", description='" + description + '\'' +
               '}';
    }
}