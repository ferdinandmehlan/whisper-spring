package io.github.ferdinandmehlan.whisperspringserver.config;

import io.github.ferdinandmehlan.whisperspring.audio.AudioDecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps client-side failures to meaningful HTTP responses.
 * The response body is a RFC 7807 {@link ProblemDetail} and never contains decoder output such as
 * ffmpeg's stderr, which may leak local paths.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Maps an audio decoding failure to a status code derived from its reason.
     *
     * @param exception the decoding failure
     * @return a problem detail response
     */
    @ExceptionHandler(AudioDecodingException.class)
    public ResponseEntity<ProblemDetail> handleAudioDecodingException(AudioDecodingException exception) {
        HttpStatus status = switch (exception.getReason()) {
            case UNSUPPORTED_FORMAT -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case DECODER_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case LIMIT_EXCEEDED -> HttpStatus.UNPROCESSABLE_CONTENT;
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
            case FAILED -> HttpStatus.UNPROCESSABLE_CONTENT;
        };
        log.warn("Audio decoding failed ({}): {}", exception.getReason(), exception.getMessage());
        log.debug("Audio decoding failure", exception);
        return problem(status, exception.getMessage());
    }

    /**
     * Maps invalid request parameters to {@code 400 Bad Request}.
     *
     * @param exception the invalid argument exception
     * @return a problem detail response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Invalid request", exception);
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private static ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        return ResponseEntity.status(status).body(problemDetail);
    }
}
