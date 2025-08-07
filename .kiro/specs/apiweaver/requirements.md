# Requirements Document

## Introduction

This document outlines the requirements for ApiWeaver, a Java command-line tool that parses HTML documentation from the TimeTap API website and generates or amends OpenAPI 3.1.1 specification files. ApiWeaver extracts property information from HTML tables that follow specific H2 elements and converts them into structured OpenAPI schema definitions.

## Requirements

### Requirement 1

**User Story:** As a developer integrating with the TimeTap API, I want to provide a URL to the command-line tool, so that I can automatically extract API object definitions from the HTML documentation.

#### Acceptance Criteria

1. WHEN the user provides a URL as a command-line argument THEN the system SHALL fetch the HTML content from that URL
2. WHEN the URL is invalid or unreachable THEN the system SHALL display an appropriate error message and exit gracefully
3. WHEN the HTML content is successfully retrieved THEN the system SHALL parse it for further processing

### Requirement 2

**User Story:** As a developer, I want the tool to locate specific HTML table structures, so that I can extract object property definitions from the TimeTap API documentation format.

#### Acceptance Criteria

1. WHEN parsing HTML content THEN the system SHALL search for H2 elements with id attributes ending in "ObjectValues"
2. WHEN the first H2 element with matching id is found THEN the system SHALL locate the first table element that follows it in the DOM
3. WHEN no matching H2 element is found THEN the system SHALL display an error message and exit
4. WHEN multiple matching H2 elements exist THEN the system SHALL process only the first one and display a warning about additional matches

### Requirement 3

**User Story:** As a developer, I want the tool to extract property information from HTML tables, so that I can generate accurate OpenAPI schema definitions.

#### Acceptance Criteria

1. WHEN a target table is found THEN the system SHALL identify columns for "Property Name", "Type", "Required", "Writable", and "Description"
2. WHEN the table headers don't match expected column names THEN the system SHALL attempt fuzzy matching or display an error
3. WHEN extracting table data THEN the system SHALL parse each row to extract property name, type, required status, writable status and description
4. WHEN a table row is malformed or missing data THEN the system SHALL log a warning and skip that row
5. WHEN all valid rows are processed THEN the system SHALL store the extracted property information for schema generation

### Requirement 4

**User Story:** As a developer, I want the tool to generate or amend OpenAPI 3.1.1 specification files, so that I can maintain up-to-date API documentation.

#### Acceptance Criteria

1. WHEN property information is extracted THEN the system SHALL generate OpenAPI 3.1.1 compliant schema definitions
2. WHEN an existing OpenAPI file is specified THEN the system SHALL merge new schema definitions with existing content
3. WHEN no existing OpenAPI file exists THEN the system SHALL create a new OpenAPI 3.1.1 specification file
4. WHEN generating schemas THEN the system SHALL map HTML property types to appropriate OpenAPI data types
5. WHEN a property is marked as required THEN the system SHALL include it in the required array of the schema
6. WHEN a property is marked as not writable THEN the system SHALL add appropriate readOnly annotations

### Requirement 5

**User Story:** As a developer, I want the tool to provide clear output and error handling, so that I can understand what was processed and troubleshoot any issues.

#### Acceptance Criteria

1. WHEN the tool starts processing THEN the system SHALL display progress information to the user
2. WHEN schemas are successfully generated THEN the system SHALL output the location of the created/updated OpenAPI file
3. WHEN errors occur during processing THEN the system SHALL display descriptive error messages with context
4. WHEN the tool completes successfully THEN the system SHALL exit with status code 0
5. WHEN the tool encounters errors THEN the system SHALL exit with appropriate non-zero status codes

### Requirement 6

**User Story:** As a developer, I want the tool to support configuration options, so that I can customize its behavior for different use cases.

#### Acceptance Criteria

1. WHEN running the tool THEN the system SHALL accept command-line options for output file path
2. WHEN no output file is specified THEN the system SHALL use a default filename based on the URL or timestamp
3. WHEN running the tool THEN the system SHALL accept options to control verbosity of output
4. WHEN running the tool THEN the system SHALL provide a help option that displays usage information
5. WHEN invalid command-line arguments are provided THEN the system SHALL display usage help and exit

### Requirement 7

**User Story:** As a developer, I want the tool to handle various HTML structures gracefully, so that it can work with different documentation formats or changes to the TimeTap documentation.

#### Acceptance Criteria

1. WHEN HTML structure varies slightly from expected format THEN the system SHALL attempt to adapt and continue processing
2. WHEN encountering nested tables or complex HTML structures THEN the system SHALL process only the target table
3. WHEN HTML contains special characters or encoding issues THEN the system SHALL handle them appropriately
4. WHEN the HTML structure changes significantly THEN the system SHALL provide clear error messages indicating what was expected vs. found
