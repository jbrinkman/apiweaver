package com.apiweaver;

import java.util.Objects;

/**
 * Configuration class that holds all command-line options and settings for ApiWeaver.
 * Uses builder pattern for flexible configuration construction.
 */
public class Configuration {
    private final String url;
    private final String outputFile;
    private final String existingSpecFile;
    private final boolean verbose;
    private final int timeoutMs;

    private Configuration(Builder builder) {
        this.url = builder.url;
        this.outputFile = builder.outputFile;
        this.existingSpecFile = builder.existingSpecFile;
        this.verbose = builder.verbose;
        this.timeoutMs = builder.timeoutMs;
    }

    public String getUrl() {
        return url;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getExistingSpecFile() {
        return existingSpecFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    /**
     * Validates that the configuration has all required values.
     * 
     * @return true if the configuration is valid
     */
    public boolean isValid() {
        return url != null && !url.trim().isEmpty() &&
               outputFile != null && !outputFile.trim().isEmpty() &&
               timeoutMs > 0;
    }

    /**
     * Creates a new builder for configuration.
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating Configuration instances.
     */
    public static class Builder {
        private String url;
        private String outputFile = "generated-api.yaml";
        private String existingSpecFile;
        private boolean verbose = false;
        private int timeoutMs = 30000;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder outputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder existingSpecFile(String existingSpecFile) {
            this.existingSpecFile = existingSpecFile;
            return this;
        }

        public Builder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public Builder timeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return verbose == that.verbose &&
               timeoutMs == that.timeoutMs &&
               Objects.equals(url, that.url) &&
               Objects.equals(outputFile, that.outputFile) &&
               Objects.equals(existingSpecFile, that.existingSpecFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, outputFile, existingSpecFile, verbose, timeoutMs);
    }

    @Override
    public String toString() {
        return "Configuration{" +
               "url='" + url + '\'' +
               ", outputFile='" + outputFile + '\'' +
               ", existingSpecFile='" + existingSpecFile + '\'' +
               ", verbose=" + verbose +
               ", timeoutMs=" + timeoutMs +
               '}';
    }
}