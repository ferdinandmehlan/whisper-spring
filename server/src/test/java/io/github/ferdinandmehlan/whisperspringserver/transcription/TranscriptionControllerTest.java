package io.github.ferdinandmehlan.whisperspringserver.transcription;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspringserver.BaseIntegrationTest;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionResponse;
import io.github.ferdinandmehlan.whisperspringtestcommon.EnabledIfFfmpeg;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TranscriptionControllerTest extends BaseIntegrationTest {

    private static final Path SAMPLE = Path.of("src/test/resources/audio/sample.wav");

    @Test
    public void testTranscriptionMinimal() {
        ResponseEntity<TranscriptionResponse> response = transcribe(multipart(SAMPLE));

        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }

    @Test
    public void testTranscriptionStreaming() {
        ResponseEntity<String> response = transcribeStreaming(multipart(SAMPLE, true));

        assertWithFileIncludingHttpStatus(response);
    }

    @Test
    @EnabledIfFfmpeg
    public void testTranscriptionOfM4a() {
        ResponseEntity<TranscriptionResponse> response =
                transcribe(multipart(Path.of("src/test/resources/audio/sample.m4a")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }

    @Test
    @EnabledIfFfmpeg
    public void testTranscriptionOfUndecodableFile(@TempDir Path tempDir) throws IOException {
        Path garbage = tempDir.resolve("not-audio.wav");
        Files.writeString(garbage, "this is definitely not an audio file");

        ResponseEntity<String> response = post("transcription", multipart(garbage), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).contains("Unsupported audio format").doesNotContain(tempDir.toString());
        assertWithFileIncludingHttpStatus(response);
    }

    @Test
    @EnabledIfFfmpeg
    public void testTranscriptionStreamingOfUndecodableFile(@TempDir Path tempDir) throws IOException {
        Path garbage = tempDir.resolve("not-audio.wav");
        Files.writeString(garbage, "this is definitely not an audio file");

        ResponseEntity<String> response = post("transcription", multipart(garbage, true), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getHeaders().getContentType()).isNotEqualTo(MediaType.TEXT_EVENT_STREAM);
        assertWithFileIncludingHttpStatus(response);
    }

    private ResponseEntity<TranscriptionResponse> transcribe(HttpEntity<MultiValueMap<String, Object>> request) {
        return post("transcription", request, TranscriptionResponse.class);
    }

    private ResponseEntity<String> transcribeStreaming(HttpEntity<MultiValueMap<String, Object>> request) {
        return post("transcription", request, String.class);
    }

    private <T> ResponseEntity<T> post(
            String path, HttpEntity<MultiValueMap<String, Object>> request, Class<T> responseType) {
        return testRestTemplate.postForEntity("/api/" + path, request, responseType);
    }

    private static HttpEntity<MultiValueMap<String, Object>> multipart(Path audioPath) {
        return multipart(audioPath, false);
    }

    private static HttpEntity<MultiValueMap<String, Object>> multipart(Path audioPath, boolean stream) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioPath));
        if (stream) {
            body.add("stream", "true");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }
}
