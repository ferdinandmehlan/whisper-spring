package io.github.ferdinandmehlan.whisperspringserver;

import de.cronn.assertions.validationfile.normalization.SimpleRegexReplacement;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainerStartupTest extends BaseIntegrationTest {

    @Container
    static final GenericContainer<?> whisperSpringServer = new GenericContainer<>(
                    "ghcr.io/ferdinandmehlan/whisper-spring-server:dev")
            .withExposedPorts(8080)
            .withCreateContainerCmdModifier(cmd -> cmd.withName("whisper-spring-server-" + UUID.randomUUID()))
            .withFileSystemBind("build/resources/test/ggml-tiny.bin", "/app/models/ggml-tiny.bin", BindMode.READ_ONLY)
            .waitingFor(Wait.forLogMessage(".*Tomcat started on port.*", 1));

    private static String getWhisperSpringServerHost() {
        return "http://%s:%d".formatted(whisperSpringServer.getHost(), whisperSpringServer.getMappedPort(8080));
    }

    private final ValidationNormalizer logNormalizer = ValidationNormalizer.combine(
            new SimpleRegexReplacement("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?[Z]?", "[TIMESTAMP]"),
            new SimpleRegexReplacement("\\d+ ms", "[TIME] ms"),
            new SimpleRegexReplacement("in \\d+\\.\\d+ seconds", "in [TIME] seconds"),
            new SimpleRegexReplacement("\\(process running for \\d+\\.\\d+\\)", "(process running for [TIME])"));

    @Test
    public void testContainerStartup() {
        // Get startup logs from the container
        String startupLogs = whisperSpringServer.getLogs();
        assertWithFileWithSuffix(startupLogs, logNormalizer, "startup_logs");

        // Load test audio file
        Path audioPath = Path.of("src/test/resources/audio/sample.wav");
        FileSystemResource audioFile = new FileSystemResource(audioPath);

        // Create multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioFile);
        body.add("responseFormat", "text");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make request to inference endpoint
        ResponseEntity<String> response = testRestTemplate.postForEntity(
                getWhisperSpringServerHost() + "/inference", requestEntity, String.class);

        // Verify response
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }
}
