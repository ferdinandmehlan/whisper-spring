package io.github.ferdinandmehlan.whisperspringserver;

import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionResponse;
import io.github.ferdinandmehlan.whisperspringtestcommon.BaseTest;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Base class for tests that run against the packaged server image instead of an in-process Spring
 * context.
 *
 * <p>The container is started once per JVM and shared by all subclasses, so that a new image does
 * not have to be booted for every test class. It is not stopped explicitly - Testcontainers' Ryuk
 * sidecar removes it when the JVM exits. Use {@link #server()} to run commands inside the container
 * and {@link #serverUrl()} to reach the HTTP API.
 */
public abstract class BaseContainerTest extends BaseTest {

    /**
     * Image under test. Override with {@code -Dwhisper.test.image=...} or the
     * {@code WHISPER_TEST_IMAGE} environment variable.
     */
    public static final String SERVER_IMAGE = System.getProperty(
            "whisper.test.image",
            System.getenv().getOrDefault("WHISPER_TEST_IMAGE", "ghcr.io/ferdinandmehlan/whisper-spring-server:dev"));

    private static final int HTTP_PORT = 8080;
    private static final String MODEL_FILE = "../build/models/ggml-tiny.bin";
    private static final String MODEL_PATH_IN_CONTAINER = "/app/models/ggml-tiny.bin";
    private static final String STARTUP_LOG_MESSAGE = ".*Started WhisperSpringServerApplication in.*";

    private static final GenericContainer<?> SERVER = startServer();

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    private static GenericContainer<?> startServer() {
        GenericContainer<?> container = new GenericContainer<>(SERVER_IMAGE)
                .withExposedPorts(HTTP_PORT)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("whisper-spring-server-" + UUID.randomUUID()))
                .withFileSystemBind(MODEL_FILE, MODEL_PATH_IN_CONTAINER, BindMode.READ_ONLY)
                .withEnv("WHISPER_MODEL_PATH", MODEL_PATH_IN_CONTAINER)
                .waitingFor(Wait.forLogMessage(STARTUP_LOG_MESSAGE, 1));
        container.start();
        return container;
    }

    /**
     * Returns the shared server container.
     *
     * @return the running server container
     */
    protected static GenericContainer<?> server() {
        return SERVER;
    }

    /**
     * Returns the base URL of the shared server container.
     *
     * @return the base URL, e.g. {@code http://localhost:32768}
     */
    protected static String serverUrl() {
        return "http://%s:%d".formatted(SERVER.getHost(), SERVER.getMappedPort(HTTP_PORT));
    }

    /**
     * Resolves a test audio file that is packaged in this module.
     *
     * @param name the file name below {@code src/test/resources/audio}
     * @return the audio file as a resource
     */
    protected static Resource audioFile(String name) {
        return new FileSystemResource(Path.of("src/test/resources/audio", name));
    }

    /**
     * Sends an audio file to the blocking transcription endpoint.
     *
     * @param file the audio file to transcribe
     * @return the response of the server
     */
    protected ResponseEntity<TranscriptionResponse> transcribe(Resource file) {
        return testRestTemplate.postForEntity(
                serverUrl() + "/api/transcription", multipartRequest(file, false), TranscriptionResponse.class);
    }

    /**
     * Sends an audio file to the streaming transcription endpoint.
     *
     * @param file the audio file to transcribe
     * @return the raw server-sent-event stream
     */
    protected ResponseEntity<String> transcribeStreaming(Resource file) {
        return testRestTemplate.postForEntity(
                serverUrl() + "/api/transcription", multipartRequest(file, true), String.class);
    }

    /**
     * Sends an audio file to the blocking transcription endpoint and keeps the body unparsed, so
     * that error responses can be asserted.
     *
     * @param file the audio file to transcribe
     * @return the raw response of the server
     */
    protected ResponseEntity<String> transcribeRaw(Resource file) {
        return testRestTemplate.postForEntity(
                serverUrl() + "/api/transcription", multipartRequest(file, false), String.class);
    }

    private static HttpEntity<MultiValueMap<String, Object>> multipartRequest(Resource file, boolean stream) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);
        if (stream) {
            body.add("stream", "true");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }
}
