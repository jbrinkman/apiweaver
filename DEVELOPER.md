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
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── apiweaver/
│   │   │           ├── cli/           # Command-line interface
│   │   │           ├── fetcher/       # URL fetching components
│   │   │           ├── parser/        # HTML parsing components
│   │   │           ├── extractor/     # Table extraction logic
│   │   │           ├── mapper/        # Property type mapping
│   │   │           ├── generator/     # OpenAPI generation
│   │   │           ├── model/         # Data models
│   │   │           └── exception/     # Exception classes
│   │   └── resources/
│   │       └── logback.xml           # Logging configuration
│   └── test/
│       ├── java/                     # Unit and integration tests
│       └── resources/                # Test resources (sample HTML files)
├── pom.xml                          # Maven configuration
├── README.md
├── LICENSE
├── DEVELOPER.md
└── CONTRIBUTING.md
```

## Architecture Overview

ApiWeaver follows a modular architecture with clear separation of concerns:

- **CLI Layer**: Handles command-line arguments and user interaction
- **Fetcher Layer**: Manages HTTP requests and URL handling
- **Parser Layer**: HTML parsing using JSoup
- **Extractor Layer**: Table data extraction and processing
- **Mapper Layer**: Type mapping between HTML and OpenAPI formats
- **Generator Layer**: OpenAPI specification generation and file operations

## Key Dependencies

- **JSoup**: HTML parsing and DOM manipulation
- **Jackson**: YAML/JSON processing for OpenAPI files
- **Apache Commons CLI**: Command-line argument parsing
- **SLF4J + Logback**: Logging framework
- **JUnit 5**: Testing framework
- **Mockito**: Mocking for unit tests
- **AssertJ**: Fluent assertions

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

### Test Data

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

### Maven Profiles

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
