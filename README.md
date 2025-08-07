# ApiWeaver

A Java command-line tool that parses HTML documentation from the TimeTap API website and generates or amends OpenAPI 3.1.1 specification files.

## Overview

ApiWeaver extracts API object definitions from TimeTap's HTML documentation by locating specific table structures and converting property information into structured OpenAPI schemas. The tool helps developers maintain up-to-date API documentation by automatically parsing HTML tables that follow specific H2 elements.

## Features

- Parse HTML documentation from URLs
- Extract property definitions from HTML tables
- Generate OpenAPI 3.1.1 compliant specifications
- Merge with existing OpenAPI files
- Command-line interface with configurable options
- Comprehensive error handling and logging

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

Generate a new OpenAPI specification:

```bash
java -jar target/apiweaver.jar https://example.com/api-docs
```

Amend an existing OpenAPI file:

```bash
java -jar target/apiweaver.jar -e existing-api.yaml -o updated-api.yaml https://example.com/api-docs
```

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Development

See [DEVELOPER.md](DEVELOPER.md) for development setup and guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please file an issue on the [GitHub issue tracker](https://github.com/jbrinkman/apiweaver/issues).
