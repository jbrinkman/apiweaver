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

### 🚧 In Development

- OpenAPI 3.1.1 specification generation
- Command-line interface with configurable options
- File merging with existing OpenAPI specifications

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

> **Note**: ApiWeaver is currently in active development. The command-line interface is not yet implemented. The current implementation provides core HTTP fetching capabilities and data models.

### Planned Usage (Coming Soon)

```bash
java -jar target/apiweaver.jar [OPTIONS] <URL>

Options:
  -o, --output <file>     Output OpenAPI file path (default: generated-api.yaml)
  -e, --existing <file>   Existing OpenAPI file to amend
  -v, --verbose          Enable verbose output
  -t, --timeout <ms>     HTTP timeout in milliseconds (default: 30000)
  -h, --help             Display help information
```

### Current Development Status

The project is being developed using a spec-driven approach. You can track progress in the `.kiro/specs/apiweaver/` directory:

- ✅ **Task 1**: Set up Maven project structure and core interfaces
- ✅ **Task 2**: Implement core data models with validation  
- ✅ **Task 3**: Implement URL fetching and HTTP handling
- ✅ **Task 4**: HTML parsing with JSoup for element extraction
- ✅ **Task 5**: Table extraction logic with fuzzy column matching
- 🚧 **Task 6**: Property type mapping (next)

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
