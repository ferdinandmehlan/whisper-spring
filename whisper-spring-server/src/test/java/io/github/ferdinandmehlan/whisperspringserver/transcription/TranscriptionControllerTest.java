package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspringserver.BaseIntegrationTest;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionResponse;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TranscriptionControllerTest extends BaseIntegrationTest {

    @Test
    public void testTranscriptionMinimal() {
        // Load test audio file
        Path audioPath = Path.of("src/test/resources/audio/sample.wav");
        FileSystemResource audioFile = new FileSystemResource(audioPath);

        // Create multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Request
        ResponseEntity<TranscriptionResponse> response =
                testRestTemplate.postForEntity("/api/transcription", requestEntity, TranscriptionResponse.class);

        // Verify response
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }

    @Test
    public void testTranscriptionStreaming() {
        // Load test audio file
        Path audioPath = Path.of("src/test/resources/audio/sample.wav");
        FileSystemResource audioFile = new FileSystemResource(audioPath);

        // Create multipart request with stream=true
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioFile);
        body.add("stream", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Request - should return SSE stream
        ResponseEntity<String> response =
                testRestTemplate.postForEntity("/api/transcription", requestEntity, String.class);

        // Verify response
        assertWithFileIncludingHttpStatus(response);
    }
}
