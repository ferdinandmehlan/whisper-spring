package io.github.ferdinandmehlan.whisperspringserver.inference;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling audio transcription inference requests.
 * Provides endpoints for transcribing audio files using Whisper models.
 */
@RestController
@RequestMapping("/api/inference")
public class InferenceController {

    private final InferenceService inferenceService;
    private final InferenceMapper inferenceMapper;

    /**
     * Creates a new InferenceController with required dependencies.
     *
     * @param inferenceService the service for performing transcription
     * @param inferenceMapper the mapper for converting between request/response objects
     */
    public InferenceController(InferenceService inferenceService, InferenceMapper inferenceMapper) {
        this.inferenceService = inferenceService;
        this.inferenceMapper = inferenceMapper;
    }

    /**
     * Handles audio transcription requests.
     * Accepts multipart form data with audio file and transcription parameters,
     * returns transcription results in the requested format.
     *
     * @param request the inference request containing audio file and parameters
     * @return ResponseEntity with transcription results in JSON, text, or SRT format
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> inference(@Valid @ModelAttribute InferenceRequest request) {
        WhisperTranscribeConfig config = inferenceMapper.toWhisperParams(request);

        List<WhisperSegment> segments =
                inferenceService.transcribe(config, request.file().getResource());

        if (request.responseFormat() == ResponseFormat.TEXT) {
            String text = inferenceMapper.toText(segments);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(text);
        } else if (request.responseFormat() == ResponseFormat.SRT) {
            String srt = inferenceMapper.toSrt(segments);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(srt);
        } else {
            InferenceResponse response = inferenceMapper.toJson(segments);
            return ResponseEntity.ok(response);
        }
    }
}
