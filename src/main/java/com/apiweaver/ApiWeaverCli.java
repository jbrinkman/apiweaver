package com.apiweaver;

import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command-line interface for ApiWeaver.
 * Handles argument parsing and orchestrates the main workflow.
 */
public class ApiWeaverCli {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiWeaverCli.class);
    
    private static final String DEFAULT_OUTPUT_FILE = "generated-api.yaml";
    private static final int DEFAULT_TIMEOUT_MS = 30000;
    
    public static void main(String[] args) {
        logger.info("Starting ApiWeaver application");
        
        try {
            ApiWeaverCli cli = new ApiWeaverCli();
            Configuration config = cli.parseArguments(args);
            
            if (config != null) {
                logger.info("Configuration parsed successfully: URL={}, Output={}", 
                    config.getUrl(), config.getOutputFile());
                
                // Execute main workflow
                cli.executeWorkflow(config);
            }
            
        } catch (ParseException e) {
            logger.error("Failed to parse command-line arguments: {}", e.getMessage());
            System.exit(1);
        } catch (ApiWeaverException e) {
            logger.error("ApiWeaver processing error: {}", e.getMessage(), e);
            System.exit(2);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            System.exit(3);
        }
        
        logger.info("ApiWeaver application completed successfully");
    }
    
    /**
     * Parses command-line arguments and returns a Configuration object.
     * 
     * @param args command-line arguments
     * @return Configuration object or null if help was displayed
     * @throws ParseException if argument parsing fails
     */
    public Configuration parseArguments(String[] args) throws ParseException {
        logger.debug("Parsing command-line arguments");
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            // Handle help option
            if (cmd.hasOption("h")) {
                logger.debug("Help option requested");
                displayHelp(options);
                return null;
            }
            
            // Get remaining arguments (should be the URL)
            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length != 1) {
                throw new ParseException("Exactly one URL argument is required");
            }
            
            String url = remainingArgs[0];
            
            // Build configuration from parsed options
            Configuration.Builder configBuilder = Configuration.builder()
                .url(url)
                .outputFile(cmd.getOptionValue("o", DEFAULT_OUTPUT_FILE))
                .verbose(cmd.hasOption("v"));
            
            // Handle existing file option
            if (cmd.hasOption("e")) {
                configBuilder.existingSpecFile(cmd.getOptionValue("e"));
            }
            
            // Handle timeout option
            if (cmd.hasOption("t")) {
                try {
                    int timeout = Integer.parseInt(cmd.getOptionValue("t"));
                    if (timeout <= 0) {
                        throw new ParseException("Timeout must be a positive integer");
                    }
                    configBuilder.timeoutMs(timeout);
                } catch (NumberFormatException e) {
                    throw new ParseException("Invalid timeout value: " + cmd.getOptionValue("t"));
                }
            } else {
                configBuilder.timeoutMs(DEFAULT_TIMEOUT_MS);
            }
            
            Configuration config = configBuilder.build();
            validateConfiguration(config);
            
            return config;
            
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            displayHelp(options);
            throw e;
        }
    }
    
    /**
     * Creates the command-line options definition.
     * 
     * @return Options object with all defined command-line options
     */
    private Options createOptions() {
        Options options = new Options();
        
        options.addOption(Option.builder("o")
            .longOpt("output")
            .hasArg()
            .argName("file")
            .desc("Output OpenAPI file path (default: " + DEFAULT_OUTPUT_FILE + ")")
            .build());
            
        options.addOption(Option.builder("e")
            .longOpt("existing")
            .hasArg()
            .argName("file")
            .desc("Existing OpenAPI file to amend")
            .build());
            
        options.addOption(Option.builder("v")
            .longOpt("verbose")
            .desc("Enable verbose output")
            .build());
            
        options.addOption(Option.builder("t")
            .longOpt("timeout")
            .hasArg()
            .argName("ms")
            .desc("HTTP timeout in milliseconds (default: " + DEFAULT_TIMEOUT_MS + ")")
            .build());
            
        options.addOption(Option.builder("h")
            .longOpt("help")
            .desc("Display help information")
            .build());
            
        return options;
    }
    
    /**
     * Displays help information to the user.
     * 
     * @param options the command-line options to display help for
     */
    private void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar apiweaver.jar [OPTIONS] <URL>", 
            "\nApiWeaver - Extract API definitions from TimeTap HTML documentation\n\nOptions:", 
            options, 
            "\nExample:\n  java -jar apiweaver.jar -o my-api.yaml -v https://example.com/api-docs\n");
    }
    
    /**
     * Validates the parsed configuration.
     * 
     * @param config the configuration to validate
     * @throws ParseException if the configuration is invalid
     */
    private void validateConfiguration(Configuration config) throws ParseException {
        if (!config.isValid()) {
            throw new ParseException("Invalid configuration: URL and output file are required");
        }
        
        // Additional validation for URL format
        String url = config.getUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new ParseException("URL must start with http:// or https://");
        }
    }
    
    /**
     * Executes the main workflow that coordinates all components.
     * 
     * @param config the validated configuration
     * @throws ApiWeaverException if any step in the workflow fails
     */
    private void executeWorkflow(Configuration config) throws ApiWeaverException {
        logger.info("Starting main workflow execution");
        
        try {
            // Step 1: Fetch HTML content
            reportProgress("Fetching HTML content from: " + config.getUrl(), config.isVerbose());
            UrlFetcher fetcher = new HttpUrlFetcher(config.getTimeoutMs(), "ApiWeaver/1.0");
            String htmlContent = fetcher.fetchHtmlContent(config.getUrl());
            logger.info("Successfully fetched {} characters of HTML content", htmlContent.length());
            
            // Step 2: Parse HTML and find target elements
            reportProgress("Parsing HTML content and locating target elements", config.isVerbose());
            HtmlParser parser = new JSoupHtmlParser();
            org.jsoup.nodes.Document doc = parser.parseHtml(htmlContent);
            
            // Find H2 elements with ObjectValues suffix - validate single match
            java.util.List<org.jsoup.nodes.Element> h2Elements = parser.findH2ElementsWithIdEndingIn(doc, "ObjectValues");
            validateH2ElementMatching(h2Elements);
            
            // Get the first (and should be only) H2 element
            org.jsoup.nodes.Element targetH2 = h2Elements.get(0);
            logger.info("Found target H2 element with id: {}", targetH2.attr("id"));
            
            // Find the table following this H2
            org.jsoup.nodes.Element targetTable = parser.findFirstTableAfterElement(doc, targetH2);
            if (targetTable == null) {
                throw new ExtractionException("No table found after H2 element with id: " + targetH2.attr("id"));
            }
            logger.info("Found target table with {} rows", targetTable.select("tr").size());
            
            // Step 3: Extract property definitions from table
            reportProgress("Extracting property definitions from table", config.isVerbose());
            TableExtractor extractor = new PropertyTableExtractor();
            java.util.List<PropertyDefinition> properties = extractor.extractProperties(targetTable);
            logger.info("Successfully extracted {} property definitions", properties.size());
            
            // Step 4: Generate or amend OpenAPI specification
            reportProgress("Generating OpenAPI specification", config.isVerbose());
            OpenApi31Generator generator = new OpenApi31Generator();
            
            // Convert PropertyDefinitions to OpenApiProperties
            java.util.List<OpenApiProperty> openApiProperties = convertToOpenApiProperties(properties);
            
            OpenApiSpec result;
            if (config.getExistingSpecFile() != null) {
                reportProgress("Amending existing OpenAPI file: " + config.getExistingSpecFile(), config.isVerbose());
                // Load existing spec
                OpenApiSpec existingSpec = loadExistingSpec(config.getExistingSpecFile());
                result = generator.generateOrAmendSpec(openApiProperties, existingSpec);
            } else {
                reportProgress("Creating new OpenAPI specification", config.isVerbose());
                result = generator.generateOrAmendSpec(openApiProperties, null);
            }
            
            // Step 5: Write output file
            reportProgress("Writing output to: " + config.getOutputFile(), config.isVerbose());
            String yamlContent = convertSpecToYaml(result);
            try (java.io.FileWriter writer = new java.io.FileWriter(config.getOutputFile())) {
                writer.write(yamlContent);
            } catch (java.io.IOException e) {
                throw new GenerationException("Failed to write output file: " + config.getOutputFile(), e);
            }
            
            // Success reporting
            reportProgress("✅ Successfully generated OpenAPI specification", true);
            System.out.println("OpenAPI specification written to: " + config.getOutputFile());
            System.out.println("Processed " + properties.size() + " property definitions");
            
            logger.info("Main workflow completed successfully");
            
        } catch (ApiWeaverException e) {
            logger.error("Workflow failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in workflow: {}", e.getMessage(), e);
            throw new ApiWeaverException("Workflow execution failed", e);
        }
    }
    
    /**
     * Validates H2 element matching according to requirements.
     * Should find exactly one match, warn if multiple found.
     * 
     * @param h2Elements the list of found H2 elements
     * @throws ExtractionException if no elements found
     */
    private void validateH2ElementMatching(java.util.List<org.jsoup.nodes.Element> h2Elements) throws ExtractionException {
        if (h2Elements.isEmpty()) {
            throw new ExtractionException("No H2 elements found with id ending in 'ObjectValues'");
        }
        
        if (h2Elements.size() > 1) {
            logger.warn("Multiple H2 elements found with 'ObjectValues' suffix ({}). Processing only the first one.", h2Elements.size());
            System.err.println("⚠️  Warning: Found " + h2Elements.size() + " H2 elements with 'ObjectValues' suffix. Processing only the first one.");
            
            // Log details about all found elements for debugging
            for (int i = 0; i < h2Elements.size(); i++) {
                var element = h2Elements.get(i);
                logger.warn("H2 element {}: id='{}', text='{}'", i + 1, element.attr("id"), element.text());
            }
        }
    }
    
    /**
     * Reports progress to the user based on verbosity settings.
     * 
     * @param message the progress message
     * @param verbose whether to display the message
     */
    private void reportProgress(String message, boolean verbose) {
        if (verbose) {
            System.out.println("[INFO] " + message);
        }
        logger.debug("Progress: {}", message);
    }
    
    /**
     * Converts PropertyDefinition objects to OpenApiProperty objects.
     * 
     * @param properties the list of property definitions
     * @return list of OpenAPI properties
     */
    private java.util.List<OpenApiProperty> convertToOpenApiProperties(java.util.List<PropertyDefinition> properties) {
        java.util.List<OpenApiProperty> openApiProperties = new java.util.ArrayList<>();
        TimeTapPropertyMapper mapper = new TimeTapPropertyMapper();
        
        for (PropertyDefinition propDef : properties) {
            try {
                OpenApiProperty openApiProp = mapper.mapToOpenApiProperty(propDef);
                openApiProperties.add(openApiProp);
                logger.debug("Converted property: {} -> {}", propDef.getName(), openApiProp.getName());
            } catch (Exception e) {
                logger.warn("Failed to convert property '{}': {}", propDef.getName(), e.getMessage());
                // Continue with other properties
            }
        }
        
        logger.info("Converted {} out of {} property definitions to OpenAPI properties", 
            openApiProperties.size(), properties.size());
        return openApiProperties;
    }
    
    /**
     * Loads an existing OpenAPI specification from file.
     * 
     * @param filePath the path to the existing spec file
     * @return the loaded OpenAPI specification
     * @throws GenerationException if the file cannot be loaded
     */
    private OpenApiSpec loadExistingSpec(String filePath) throws GenerationException {
        try {
            logger.info("Loading existing OpenAPI specification from: {}", filePath);
            
            // Read the file content
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            if (!java.nio.file.Files.exists(path)) {
                throw new GenerationException("Existing spec file not found: " + filePath);
            }
            
            String yamlContent = java.nio.file.Files.readString(path, java.nio.charset.StandardCharsets.UTF_8);
            
            // Parse YAML to OpenApiSpec
            com.fasterxml.jackson.databind.ObjectMapper yamlMapper = new com.fasterxml.jackson.databind.ObjectMapper(
                new com.fasterxml.jackson.dataformat.yaml.YAMLFactory());
            
            OpenApiSpec spec = yamlMapper.readValue(yamlContent, OpenApiSpec.class);
            logger.info("Successfully loaded existing OpenAPI specification");
            return spec;
            
        } catch (java.io.IOException e) {
            logger.error("Failed to load existing spec file: {}", e.getMessage());
            throw new GenerationException("Failed to load existing OpenAPI specification from: " + filePath, e);
        }
    }
    
    /**
     * Converts an OpenApiSpec object to YAML string.
     * 
     * @param spec the OpenAPI specification
     * @return YAML representation of the spec
     * @throws GenerationException if conversion fails
     */
    private String convertSpecToYaml(OpenApiSpec spec) throws GenerationException {
        try {
            logger.debug("Converting OpenAPI spec to YAML format");
            
            com.fasterxml.jackson.databind.ObjectMapper yamlMapper = new com.fasterxml.jackson.databind.ObjectMapper(
                new com.fasterxml.jackson.dataformat.yaml.YAMLFactory());
            
            // Configure mapper for clean YAML output
            yamlMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            yamlMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT, true);
            
            String yamlContent = yamlMapper.writeValueAsString(spec);
            logger.debug("Successfully converted spec to YAML ({} characters)", yamlContent.length());
            return yamlContent;
            
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Failed to convert OpenAPI spec to YAML: {}", e.getMessage());
            throw new GenerationException("Failed to convert OpenAPI specification to YAML", e);
        }
    }
}