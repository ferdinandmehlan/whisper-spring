package io.github.ferdinandmehlan.whisperspringserver.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspring.audio.AudioDecodingException;
import io.github.ferdinandmehlan.whisperspring.audio.AudioDecodingException.Reason;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsUnsupportedFormatToUnsupportedMediaType() {
        assertStatus(Reason.UNSUPPORTED_FORMAT, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void mapsUnavailableDecoderToServiceUnavailable() {
        assertStatus(Reason.DECODER_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void mapsExceededLimitToUnprocessableEntity() {
        assertStatus(Reason.LIMIT_EXCEEDED, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void mapsTimeoutToGatewayTimeout() {
        assertStatus(Reason.TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
    }

    @Test
    void mapsUnexpectedFailureToUnprocessableEntity() {
        assertStatus(Reason.FAILED, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void mapsIllegalArgumentToBadRequest() {
        ResponseEntity<ProblemDetail> response =
                handler.handleIllegalArgumentException(new IllegalArgumentException("Audio file does not exist"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("Audio file does not exist");
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
    }

    @Test
    void doesNotLeakTheCauseInTheBody() {
        ResponseEntity<ProblemDetail> response = handler.handleAudioDecodingException(new AudioDecodingException(
                Reason.FAILED, "Failed to read the uploaded audio file.", new IllegalStateException("/tmp/secret")));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).doesNotContain("/tmp/secret");
    }

    private void assertStatus(Reason reason, HttpStatus expected) {
        ResponseEntity<ProblemDetail> response =
                handler.handleAudioDecodingException(new AudioDecodingException(reason, "message"));

        assertThat(response.getStatusCode()).isEqualTo(expected);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(expected.value());
        assertThat(response.getBody().getDetail()).isEqualTo("message");
    }
}
