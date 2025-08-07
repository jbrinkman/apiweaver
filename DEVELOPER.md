# Developer Guide

This document provides information for developers who want to contribute to or modify ApiWeaver.

## Development Environment Setup

### Prerequisites

- Java 11 or higher (OpenJDK recommended)
- Maven 3.6 or higher
- Git
- IDE of choice (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/jbrinkman/apiweaver.git
   cd apiweaver
   ```

2. Build the project:

   ```bash
   mvn clean compile
   ```

3. Run tests:

   ```bash
   mvn test
   ```

4. Create executable JAR:

   ```bash
   mvn clean package
   ```

## Project Structure

```
apiweaver/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── apiweaver/
│   │               ├── ApiWeaverCli.java       # Command-line interface
│   │               ├── Configuration.java      # Configuration management
│   │               ├── ExtractionException.java # Table extraction exceptions
│   │               ├── FetchException.java     # HTTP fetching exceptions
│   │               ├── HtmlParser.java         # HTML parsing interface
│   │               ├── HttpUrlFetcher.java     # HTTP URL fetching implementation
│   │               ├── JSoupHtmlParser.java    # JSoup-based HTML parser implementation
│   │               ├── OpenApi31Generator.java # OpenAPI 3.1.1 specification generator
│   │               ├── OpenApiGenerator.java   # OpenAPI generation interface
│   │               ├── OpenApiProperty.java    # OpenAPI property model
│   │               ├── OpenApiSpec.java        # OpenAPI specification model
│   │               ├── PropertyDefinition.java # Property definition model
│   │               ├── PropertyMapper.java     # Property mapping interface
│   │               ├── PropertyTableExtractor.java # Table extraction implementation
│   │               ├── TableExtractor.java     # Table extraction interface
│   │               ├── TimeTapPropertyMapper.java # HTML to OpenAPI type mapper
│   │               └── UrlFetcher.java         # URL fetching interface
│   └── test/
│       └── java/
│           └── com/
│               └── apiweaver/
│                   ├── ApiWeaverCliTest.java
│                   ├── ConfigurationTest.java
│                   ├── HttpUrlFetcherTest.java
│                   ├── JSoupHtmlParserTest.java
│                   ├── OpenApi31GeneratorTest.java
│                   ├── OpenApiPropertyTest.java
│                   ├── OpenApiSpecTest.java
│                   ├── PropertyDefinitionTest.java
│                   ├── PropertyTableExtractorTest.java
│                   └── TimeTapPropertyMapperTest.java
├── .kiro/
│   ├── specs/
│   │   └── apiweaver/              # Feature specifications
│   └── steering/                   # Development rules and guidelines
├── pom.xml                         # Maven configuration
├── README.md
├── DEVELOPER.md
└── CONTRIBUTING.md
```

## Architecture Overview

ApiWeaver follows a modular architecture with clear separation of concerns:

### Current Implementation Status

#### ✅ Completed Components

- **Core Models**: Data structures for OpenAPI properties, specifications, and property definitions
- **Configuration Management**: Centralized configuration handling with validation
- **HTTP Fetching**: URL fetching with timeout and user-agent configuration via `HttpUrlFetcher`
- **HTML Parsing**: JSoup-based HTML parsing with `JSoupHtmlParser` for element extraction
- **Table Extraction**: Property extraction from HTML tables with `PropertyTableExtractor`
- **Property Type Mapping**: HTML to OpenAPI type conversion with `TimeTapPropertyMapper`
- **OpenAPI Generation**: OpenAPI 3.1.1 specification generation with `OpenApi31Generator`
- **CLI Layer**: Command-line interface with Apache Commons CLI for argument parsing
- **Error Handling**: Comprehensive exception handling with `FetchException` and `ExtractionException`

#### 🚧 Planned Components

- **Main Workflow**: Complete orchestration of all components with progress reporting
- **Enhanced Logging**: SLF4J-based logging throughout the application
- **Integration Testing**: Comprehensive workflow validation and component coordination tests

### Architecture Details

#### HTTP Fetching Layer

- `UrlFetcher` interface provides abstraction for HTTP operations
- `HttpUrlFetcher` implements HTTP/HTTPS fetching using Java's built-in `HttpURLConnection`
- Configurable timeout and user-agent settings
- Protocol validation (HTTP/HTTPS only)
- Comprehensive error handling for network failures, timeouts, and invalid URLs

#### HTML Parsing Layer

- `HtmlParser` interface provides abstraction for HTML parsing operations
- `JSoupHtmlParser` implements HTML parsing using the JSoup library
- Finds H2 elements with id attributes ending in specific suffixes (e.g., "ObjectValues")
- Locates first table element following a given element in document order
- Handles various HTML structures and provides detailed error messages

#### Error Handling and Logging

**Exception Hierarchy**
- `ApiWeaverException`: Base exception class with contextual information support
- `FetchException`: HTTP/network-related errors during URL fetching
- `ParseException`: HTML parsing and DOM manipulation errors
- `ExtractionException`: Table extraction and property parsing errors
- `GenerationException`: OpenAPI specification generation errors
- `ConfigurationException`: Configuration validation and setup errors

**Logging Framework**
- SLF4J API with Logback Classic implementation
- Configurable log levels: DEBUG, INFO, WARN, ERROR
- Multiple appenders: console, file, and rolling error logs
- Component-specific loggers for different subsystems
- Rolling log files with daily rotation and 30-day retention
- Separate error log files for easier debugging

**Error Recovery**
- Graceful degradation when possible (e.g., skipping malformed table rows)
- Detailed error messages with context for debugging
- Proper resource cleanup in error scenarios
- Validation at component boundaries to fail fast

#### Main Workflow Orchestration

**Workflow Coordination**
- `ApiWeaverCli.executeWorkflow()`: Coordinates all components in proper sequence
- Step-by-step execution: URL fetching → HTML parsing → element validation → table extraction → OpenAPI generation → file output
- Comprehensive error handling with specific exit codes for different failure types
- Progress reporting with configurable verbosity levels

**H2 Element Validation**
- Validates exactly one H2 element with "ObjectValues" suffix is found
- Issues warnings when multiple matching elements are detected
- Processes only the first matching element for consistency
- Detailed logging of all found elements for debugging

**Integration Testing**
- `WorkflowIntegrationTest`: Tests complete workflow coordination
- Validates argument parsing and configuration handling
- Tests H2 element validation logic with multiple scenarios
- Verifies progress reporting and helper method functionality

#### Table Extraction Layer

- `TableExtractor` interface provides abstraction for extracting property data from HTML tables
- `PropertyTableExtractor` implements table extraction with fuzzy column matching
- Identifies columns for Property Name, Type, Required, Writable, and Description using pattern matching
- Supports various header formats and uses fuzzy matching for flexibility
- Parses boolean values in multiple formats (true/false, yes/no, 1/0, required/mandatory)
- Handles malformed rows gracefully by skipping invalid data and continuing processing
- Detects header-like content in first rows and adjusts parsing accordingly

#### OpenAPI Generation Layer

- `OpenApiGenerator` interface provides abstraction for OpenAPI specification generation
- `OpenApi31Generator` implements OpenAPI 3.1.1 compliant schema generation using Jackson
- Creates new OpenAPI specifications with proper structure and metadata
- Loads and merges with existing OpenAPI files while preserving existing schemas
- Converts extracted properties to OpenAPI schema format with proper type mapping
- Handles required properties, read-only flags, and property descriptions
- Supports YAML file I/O operations with proper error handling

## Key Dependencies

### Runtime Dependencies

- **Java 11+**: Core runtime requirement
- **Built-in HttpURLConnection**: HTTP client for URL fetching
- **JSoup 1.17.2**: HTML parsing and DOM manipulation
- **Jackson Core 2.16.1**: JSON/YAML processing core functionality
- **Jackson Databind 2.16.1**: Object mapping for JSON/YAML
- **Jackson YAML 2.16.1**: YAML format support for OpenAPI files
- **Apache Commons CLI 1.6.0**: Command-line argument parsing

### Test Dependencies

- **JUnit 5**: Testing framework
- **Mockito**: Mocking for unit tests

### Planned Dependencies

- **SLF4J + Logback**: Logging framework (to be added)

## Testing Strategy

### Unit Tests

- Test individual components in isolation
- Use mocking for external dependencies
- Focus on edge cases and error conditions
- Maintain high code coverage (target: >80%)

### Integration Tests

- Test component interactions
- Use real HTML samples for parsing tests
- Test file I/O operations
- Validate complete workflow scenarios

### Current Test Coverage

- **ApiWeaverCli**: 17 comprehensive tests covering argument parsing, validation, and error handling
- **HttpUrlFetcher**: 16 comprehensive unit tests covering all error scenarios
- **JSoupHtmlParser**: 19 tests covering HTML parsing, element finding, and table extraction
- **PropertyTableExtractor**: 13 tests covering table extraction, column matching, and edge cases
- **Configuration**: 15 tests for configuration validation and management
- **OpenApiProperty**: 11 tests for property model validation
- **OpenApiSpec**: 9 tests for specification model handling
- **PropertyDefinition**: 11 tests for property definition validation
- **TimeTapPropertyMapper**: 23 tests for HTML to OpenAPI type mapping
- **OpenApi31Generator**: 10 comprehensive tests for OpenAPI generation, file I/O, and schema merging
- **ErrorHandlingTest**: 23 comprehensive tests covering all error scenarios and exception handling across components
- **WorkflowIntegrationTest**: 7 integration tests for complete workflow orchestration and component coordination

### Test Data (Planned)

- Sample HTML files in `src/test/resources/html/`
- Expected OpenAPI outputs in `src/test/resources/openapi/`
- Mock HTTP responses for network testing

## Code Style and Standards

### Java Conventions

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and small (< 20 lines when possible)
- Add JavaDoc for public APIs
- Use builder pattern for complex objects

### Error Handling

- Use specific exception types from the `exception` package
- Include contextual information in error messages
- Log errors at appropriate levels
- Fail fast for invalid configurations

### Logging

- Use SLF4J for all logging
- Log at appropriate levels (DEBUG, INFO, WARN, ERROR)
- Include contextual information in log messages
- Avoid logging sensitive information

## Building and Packaging

### Current Maven Configuration

- **Java 11**: Source and target compatibility
- **JSoup**: HTML parsing library (version 1.17.2)
- **Jackson**: JSON/YAML processing (version 2.16.1)
- **Apache Commons CLI**: Command-line argument parsing (version 1.6.0)
- **JUnit 5**: Testing framework (version 5.10.1)
- **Mockito**: Mocking framework (version 5.7.0)
- **Maven Compiler Plugin**: Version 3.11.0

### Maven Profiles (Planned)

- `default`: Standard build with unit tests
- `integration-test`: Includes integration tests  
- `release`: Optimized build for releases

### Creating Releases

1. Update version in `pom.xml`
2. Run full test suite: `mvn clean verify`
3. Create release build: `mvn clean package -Prelease`
4. Tag the release: `git tag -a v1.0.0 -m "Release version 1.0.0"`
5. Push tags: `git push origin --tags`

## Debugging

### Common Issues

- **HTML Structure Changes**: Update parsing logic in `JSoupHtmlParser`
- **Type Mapping Issues**: Check `TimeTapPropertyMapper` mappings
- **Network Timeouts**: Adjust timeout configuration
- **File Permission Issues**: Verify write permissions for output directory

### Debug Logging

Enable debug logging by setting the log level:

```bash
java -Dlogback.configurationFile=logback-debug.xml -jar target/apiweaver.jar
```

## Performance Considerations

- Use streaming for large HTML documents
- Implement connection pooling for multiple URLs
- Cache parsed HTML structures when possible
- Optimize memory usage for large table processing

## Contributing Workflow

1. Create feature branch from `main`
2. Implement changes with tests
3. Run full test suite
4. Update documentation if needed
5. Submit pull request
6. Address code review feedback

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed contribution guidelines.
