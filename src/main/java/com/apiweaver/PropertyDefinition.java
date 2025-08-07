package com.apiweaver;

import java.util.Objects;

/**
 * Represents a property definition extracted from HTML documentation.
 * Contains all the information needed to generate an OpenAPI property.
 */
public class PropertyDefinition {
    private final String name;
    private final String type;
    private final boolean required;
    private final boolean writable;
    private final String description;

    /**
     * Creates a new property definition.
     * 
     * @param name the property name
     * @param type the property type as specified in HTML
     * @param required whether the property is required
     * @param writable whether the property is writable
     * @param description the property description
     */
    public PropertyDefinition(String name, String type, boolean required, boolean writable, String description) {
        this.name = Objects.requireNonNull(name, "Property name cannot be null");
        this.type = Objects.requireNonNull(type, "Property type cannot be null");
        this.required = required;
        this.writable = writable;
        this.description = description != null ? description : "";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isWritable() {
        return writable;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Validates that this property definition has valid data.
     * 
     * @return true if the property definition is valid
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() && 
               type != null && !type.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyDefinition that = (PropertyDefinition) o;
        return required == that.required &&
               writable == that.writable &&
               Objects.equals(name, that.name) &&
               Objects.equals(type, that.type) &&
               Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, required, writable, description);
    }

    @Override
    public String toString() {
        return "PropertyDefinition{" +
               "name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", required=" + required +
               ", writable=" + writable +
               ", description='" + description + '\'' +
               '}';
    }
}