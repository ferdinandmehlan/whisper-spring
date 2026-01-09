package io.github.ferdinandmehlan.whisperspringserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Whisper Spring server.
 * Initializes the application and ensures native Whisper libraries are loaded.
 */
@SpringBootApplication
public class WhisperSpringServerApplication {

    /**
     * Main entry point for the Whisper Spring server application.
     * Loads native Whisper libraries before Spring context initialization.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WhisperSpringServerApplication.class, args);
    }
}
