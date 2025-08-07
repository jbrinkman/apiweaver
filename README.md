# ApiWeaver

A Java command-line tool that parses HTML documentation from the TimeTap API website and generates or amends OpenAPI 3.1.1 specification files.

## Overview

ApiWeaver extracts API object definitions from TimeTap's HTML documentation by locating specific table structures and converting property information into structured OpenAPI schemas. The tool helps developers maintain up-to-date API documentation by automatically parsing HTML tables that follow specific H2 elements.

## Features

### ✅ Currently Implemented

- HTTP/HTTPS URL fetching with configurable timeout and user-agent
- HTML parsing with JSoup for extracting specific elements
- H2 element detection with id suffix matching (e.g., "ObjectValues")
- Table extraction following H2 headers in document order
- Comprehensive error handling for network and parsing operations
- Core data models for OpenAPI specifications and properties
- Configuration management with validation
- Extensive unit test coverage

### ✅ Currently Implemented

- Property definition extraction from HTML tables with fuzzy column matching
- Column identification for Property Name, Type, Required, Writable, and Description
- Row parsing with validation and error handling for malformed data
- Support for various boolean value formats (true/false, yes/no, 1/0, etc.)
- Adaptive parsing for different HTML table structures
- Property type mapping from HTML types to OpenAPI 3.1.1 compliant types
- Support for TimeTap-specific types (id, uuid, email, url, etc.)
- Handling of array notation, nullable types, and complex type expressions
- OpenAPI 3.1.1 specification generation with Jackson YAML processing
- Schema creation from extracted properties with proper type mapping
- Loading and merging with existing OpenAPI specification files
- Command-line interface with Apache Commons CLI for argument parsing
- Support for output file, existing file, verbose, timeout, and help options
- Comprehensive error handling with structured exception hierarchy
- SLF4J logging integration with configurable log levels and file output
- Robust error recovery and detailed error reporting for debugging

- Main workflow orchestration with complete component coordination
- Progress reporting and user feedback with verbose mode support
- H2 element validation with warnings for multiple matches
- Integration tests for complete workflow scenarios

### 🚧 In Development

- Maven build configuration and executable JAR creation

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/jbrinkman/apiweaver.git
   cd apiweaver
   ```

2. Build the project:

   ```bash
   mvn clean package
   ```

3. The executable JAR will be created in the `target` directory.

## Usage

> **Note**: ApiWeaver is currently in active development. The command-line interface has been implemented but the main workflow orchestration is still in progress.

### Command-Line Interface

```bash
java -jar target/apiweaver.jar [OPTIONS] <URL>

Options:
  -o, --output <file>     Output OpenAPI file path (default: generated-api.yaml)
  -e, --existing <file>   Existing OpenAPI file to amend
  -v, --verbose          Enable verbose output
  -t, --timeout <ms>     HTTP timeout in milliseconds (default: 30000)
  -h, --help             Display help information
```

### Examples

```bash
# Basic usage with default output file
java -jar target/apiweaver.jar https://api.timetap.com/docs

# Specify custom output file
java -jar target/apiweaver.jar -o my-api.yaml https://api.timetap.com/docs

# Amend existing OpenAPI file with verbose output
java -jar target/apiweaver.jar -e existing-api.yaml -v https://api.timetap.com/docs

# Set custom timeout
java -jar target/apiweaver.jar -t 60000 https://api.timetap.com/docs
```

### Current Development Status

The project is being developed using a spec-driven approach. You can track progress in the `.kiro/specs/apiweaver/` directory:

- ✅ **Task 1**: Set up Maven project structure and core interfaces
- ✅ **Task 2**: Implement core data models with validation  
- ✅ **Task 3**: Implement URL fetching and HTTP handling
- ✅ **Task 4**: HTML parsing with JSoup for element extraction
- ✅ **Task 5**: Table extraction logic with fuzzy column matching
- ✅ **Task 6**: Property type mapping from HTML to OpenAPI types
- ✅ **Task 7**: OpenAPI specification generation with YAML processing
- ✅ **Task 8**: Command-line interface implementation with Apache Commons CLI
- 🚧 **Task 9**: Error handling and logging (next)

### For Developers

To run the current test suite:

```bash
mvn test
```

To build the project:

```bash
mvn clean package
```

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Development

See [DEVELOPER.md](DEVELOPER.md) for development setup and guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please file an issue on the [GitHub issue tracker](https://github.com/jbrinkman/apiweaver/issues).
