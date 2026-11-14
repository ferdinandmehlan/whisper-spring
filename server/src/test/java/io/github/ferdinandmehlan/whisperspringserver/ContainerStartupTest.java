package io.github.ferdinandmehlan.whisperspringserver;

import static org.assertj.core.api.Assertions.assertThat;

import de.cronn.assertions.validationfile.normalization.SimpleRegexReplacement;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class ContainerStartupTest extends BaseContainerTest {

    private final ValidationNormalizer logNormalizer = ValidationNormalizer.combine(
            new SimpleRegexReplacement("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?[Z]?", "[TIMESTAMP]"),
            new SimpleRegexReplacement("\\d+ ms", "[TIME] ms"),
            new SimpleRegexReplacement("in \\d+\\.\\d+ seconds", "in [TIME] seconds"),
            new SimpleRegexReplacement("\\(process running for \\d+\\.\\d+\\)", "(process running for [TIME])"),
            new SimpleRegexReplacement("nio-8080-exec-\\d+", "nio-8080-exec-N"));

    private static final String STARTED_MARKER = "Started WhisperSpringServerApplication in";

    @Test
    public void testContainerStartup() {
        String logs = server().getLogs();
        int started = logs.indexOf(STARTED_MARKER);
        assertThat(started).isNotNegative();
        String startupLogs = logs.substring(0, logs.indexOf('\n', started) + 1);
        assertWithFileWithSuffix(startupLogs, logNormalizer, "startup_logs");

        // Request
        ResponseEntity<TranscriptionResponse> response = transcribe(audioFile("sample.wav"));

        // Verify response
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }
}
