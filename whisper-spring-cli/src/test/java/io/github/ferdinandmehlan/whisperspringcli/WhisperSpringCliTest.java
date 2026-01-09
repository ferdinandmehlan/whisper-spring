package io.github.ferdinandmehlan.whisperspringcli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

public class WhisperSpringCliTest extends BaseIntegrationTest {

    @Test
    public void testCliWithTinyModelAndPrintFlags() throws IOException {
        // Given
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        assertThat(audioFile.exists()).isTrue();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        WhisperCliCommand command = new WhisperCliCommand(whisperService, new PrintStream(out), new PrintStream(err));

        // Configure command with tiny model and print flags
        command.model = "src/test/resources/test-models/ggml-tiny.bin";
        command.file = Path.of("src/test/resources/audio/sample.wav");
        command.printProgress = true;
        command.printColors = false;
        command.noTimestamps = false;

        // When
        command.runWithoutExit();

        // Then
        assertWithFileWithSuffix(out.toString(), "out");
        assertWithFileWithSuffix(err.toString(), "err");
    }
}
