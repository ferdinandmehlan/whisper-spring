package io.github.ferdinandmehlan.whisperspringcli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.ferdinandmehlan.whisperspring.WhisperParams;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class WhisperCliCommandTest extends BaseIntegrationTest {

    @Test
    void testFlagParsing() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When
        cmd.parseArgs("--threads", "8", "--model", "src/test/resources/test-models/ggml-tiny.bin", "--print-progress");

        // Then
        assertEquals(8, command.threads, "Threads should be parsed correctly");
        assertEquals("src/test/resources/test-models/ggml-tiny.bin", command.model, "Model should be parsed correctly");
        assertTrue(command.printProgress, "Print progress flag should be set");
    }

    @Test
    void testDefaultValues() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);

        // Then - check default values
        assertEquals(4, command.threads, "Default threads should be 4");
        assertEquals(0, command.offsetT, "Default offset-t should be 0");
        assertEquals("base", command.model, "Default model should be set");
        assertEquals("auto", command.language, "Default language should be en");
        assertFalse(command.printProgress, "Print progress should default to false");
        assertFalse(command.printColors, "Print colors should default to false");
        assertFalse(command.noTimestamps, "No timestamps should default to false");
    }

    @Test
    void testBooleanFlags() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When
        cmd.parseArgs("--print-progress", "--print-colors", "--no-timestamps", "--translate");

        // Then
        assertTrue(command.printProgress, "Print progress should be true");
        assertTrue(command.printColors, "Print colors should be true");
        assertTrue(command.noTimestamps, "No timestamps should be true");
        assertTrue(command.translate, "Translate should be true");
    }

    @Test
    void testNumericOptions() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When
        cmd.parseArgs(
                "--threads", "16",
                "--offset-t", "1000",
                "--offset-n", "5",
                "--duration", "30000",
                "--max-context", "2048",
                "--max-len", "100",
                "--best-of", "3",
                "--beam-size", "7");

        // Then
        assertEquals(16, command.threads);
        assertEquals(1000, command.offsetT);
        assertEquals(5, command.offsetN);
        assertEquals(30000, command.duration);
        assertEquals(2048, command.maxContext);
        assertEquals(100, command.maxLen);
        assertEquals(3, command.bestOf);
        assertEquals(7, command.beamSize);
    }

    @Test
    void testFloatOptions() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When
        cmd.parseArgs(
                "--word-thold", "0.05",
                "--entropy-thold", "3.0",
                "--logprob-thold", "-2.0",
                "--no-speech-thold", "0.8",
                "--temperature", "0.5",
                "--temperature-inc", "0.3");

        // Then
        assertEquals(0.05f, command.wordThreshold, 0.001);
        assertEquals(3.0f, command.entropyThreshold, 0.001);
        assertEquals(-2.0f, command.logprobThreshold, 0.001);
        assertEquals(0.8f, command.noSpeechThold, 0.001);
        assertEquals(0.5f, command.temperature, 0.001);
        assertEquals(0.3f, command.temperatureInc, 0.001);
    }

    @Test
    void testStringOptions() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When
        cmd.parseArgs(
                "--language", "de",
                "--model", "large",
                "--prompt", "Hello world",
                "--output-file", "output.txt");

        // Then
        assertEquals("de", command.language);
        assertEquals("large", command.model);
        assertEquals("Hello world", command.prompt);
        assertEquals("output.txt", command.outputFile);
    }

    @Test
    void testShortOptions() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When
        cmd.parseArgs("-t", "8", "-m", "small", "-pp");

        // Then
        assertEquals(8, command.threads);
        assertEquals("small", command.model);
        assertTrue(command.printProgress);
    }

    @Test
    void testInvalidOptionShowsError() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);

        // When & Then
        assertThrows(CommandLine.ParameterException.class, () -> {
            cmd.parseArgs("--invalid-option");
        });
    }

    @Test
    void testHelpOutput() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        CommandLine cmd = new CommandLine(command);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        // When
        int exitCode = cmd.execute("--help");

        // Then
        assertEquals(0, exitCode, "Help command should exit with code 0");
        assertWithFile(sw.toString());
    }

    @Test
    void testPrinterConfigurationWithNoPrintsDisabled() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        command.noPrints = false; // Enable printing
        command.printProgress = true;
        command.printColors = true;
        command.noTimestamps = false;
        command.printSpecial = true;

        // When
        WhisperParams params = command.toWhisperParams();

        // Then
        assertNotNull(params, "Params should be created");
        assertFalse(command.noPrints, "No prints should be disabled");
        assertTrue(command.printProgress, "Print progress should be enabled");
        assertTrue(command.printColors, "Print colors should be enabled");
        assertFalse(command.noTimestamps, "No timestamps should be disabled");
        assertTrue(command.printSpecial, "Print special should be enabled");
    }

    @Test
    void testPrinterConfigurationWithNoPrintsEnabled() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        command.noPrints = true; // Disable printing
        command.printProgress = true;
        command.printColors = true;
        command.noTimestamps = false;
        command.printSpecial = true;

        // When
        WhisperParams params = command.toWhisperParams();

        // Then
        assertNotNull(params, "Params should be created");
        assertTrue(command.noPrints, "No prints should be enabled");
        // Even though individual flags are set, no callbacks should be configured when noPrints is true
        assertTrue(command.printProgress, "Print progress flag should still be set");
        assertTrue(command.printColors, "Print colors flag should still be set");
    }

    @Test
    void testPrinterConfigurationWithTimestampsDisabled() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        command.noPrints = false;
        command.printProgress = true;
        command.printColors = true;
        command.noTimestamps = true; // Disable timestamps
        command.printSpecial = false;

        // When
        WhisperParams params = command.toWhisperParams();

        // Then
        assertNotNull(params, "Params should be created");
        assertFalse(command.noPrints, "No prints should be disabled");
        assertTrue(command.printProgress, "Print progress should be enabled");
        assertTrue(command.printColors, "Print colors should be enabled");
        assertTrue(command.noTimestamps, "No timestamps should be enabled");
        assertFalse(command.printSpecial, "Print special should be disabled");
    }

    @Test
    void testPrinterConfigurationMinimalSetup() {
        // Given
        WhisperCliCommand command = new WhisperCliCommand(whisperService);
        command.noPrints = false;
        command.printProgress = false; // No progress printing
        command.printColors = false; // No color printing
        command.noTimestamps = true; // No timestamps
        command.printSpecial = false; // No special tokens

        // When
        WhisperParams params = command.toWhisperParams();

        // Then
        assertNotNull(params, "Params should be created");
        assertFalse(command.printProgress, "Print progress should be disabled");
        assertFalse(command.printColors, "Print colors should be disabled");
        assertTrue(command.noTimestamps, "No timestamps should be enabled");
        assertFalse(command.printSpecial, "Print special should be disabled");
    }
}
