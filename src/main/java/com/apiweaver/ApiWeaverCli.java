package com.apiweaver;

import org.apache.commons.cli.*;

/**
 * Command-line interface for ApiWeaver.
 * Handles argument parsing and orchestrates the main workflow.
 */
public class ApiWeaverCli {
    
    private static final String DEFAULT_OUTPUT_FILE = "generated-api.yaml";
    private static final int DEFAULT_TIMEOUT_MS = 30000;
    
    public static void main(String[] args) {
        ApiWeaverCli cli = new ApiWeaverCli();
        try {
            Configuration config = cli.parseArguments(args);
            if (config != null) {
                // TODO: Implement main workflow orchestration
                System.out.println("Configuration parsed successfully: " + config);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Parses command-line arguments and returns a Configuration object.
     * 
     * @param args command-line arguments
     * @return Configuration object or null if help was displayed
     * @throws ParseException if argument parsing fails
     */
    public Configuration parseArguments(String[] args) throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            // Handle help option
            if (cmd.hasOption("h")) {
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
}