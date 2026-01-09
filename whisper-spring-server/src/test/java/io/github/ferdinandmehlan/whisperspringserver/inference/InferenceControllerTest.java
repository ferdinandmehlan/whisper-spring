package io.github.ferdinandmehlan.whisperspringserver.inference;

import io.github.ferdinandmehlan.whisperspringserver.BaseIntegrationTest;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class InferenceControllerTest extends BaseIntegrationTest {

    @Test
    public void testInferenceMinimalTextFormat() {
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

        // Request
        ResponseEntity<String> response = testRestTemplate.postForEntity("/inference", requestEntity, String.class);

        // Verify response
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }

    @Test
    public void testInferenceMinimalSrtFormat() {
        // Load test audio file
        Path audioPath = Path.of("src/test/resources/audio/sample.wav");
        FileSystemResource audioFile = new FileSystemResource(audioPath);

        // Create multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioFile);
        body.add("responseFormat", "srt");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Request
        ResponseEntity<String> response = testRestTemplate.postForEntity("/inference", requestEntity, String.class);

        // Verify response
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }

    @Test
    public void testInferenceMinimalJsonFormat() {
        // Load test audio file
        Path audioPath = Path.of("src/test/resources/audio/sample.wav");
        FileSystemResource audioFile = new FileSystemResource(audioPath);

        // Create multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioFile);
        body.add("responseFormat", "json");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Request
        ResponseEntity<InferenceResponse> response =
                testRestTemplate.postForEntity("/inference", requestEntity, InferenceResponse.class);

        // Verify response
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }
}
