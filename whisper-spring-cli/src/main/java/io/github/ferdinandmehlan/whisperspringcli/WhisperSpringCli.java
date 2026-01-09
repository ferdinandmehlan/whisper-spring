package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring.WhisperService;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import picocli.CommandLine;

/**
 * Main Spring Boot application class for the Whisper CLI tool.
 * Initializes the application and sets up the command line interface.
 */
@SpringBootApplication
public class WhisperSpringCli {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WhisperSpringCli.class);
        app.setLogStartupInfo(false);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    /**
     * Creates a CommandLineRunner bean that executes the Whisper CLI command.
     *
     * @param service the WhisperService instance for transcription
     * @return CommandLineRunner that runs the CLI command and exits with appropriate code
     */
    @Bean
    CommandLineRunner runner(WhisperService service) {
        return args -> {
            WhisperCliCommand command = new WhisperCliCommand(service);
            int exitCode = new CommandLine(command).execute(args);
            System.exit(exitCode);
        };
    }
}
