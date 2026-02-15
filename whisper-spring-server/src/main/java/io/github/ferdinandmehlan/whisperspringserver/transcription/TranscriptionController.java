package io.github.ferdinandmehlan.whisperspringserver.transcription;

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
 * REST controller for handling audio transcription requests.
 * Provides endpoints for transcribing audio files using Whisper models.
 */
@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private final TranscriptionMapper transcriptionMapper;

    /**
     * Creates a new TranscriptionController with required dependencies.
     *
     * @param transcriptionService the service for performing transcription
     * @param transcriptionMapper the mapper for converting between request/response objects
     */
    public TranscriptionController(TranscriptionService transcriptionService, TranscriptionMapper transcriptionMapper) {
        this.transcriptionService = transcriptionService;
        this.transcriptionMapper = transcriptionMapper;
    }

    /**
     * Handles audio transcription requests.
     * Accepts multipart form data with audio file and transcription parameters,
     * returns transcription results in the requested format.
     *
     * @param request the transcription request containing audio file and parameters
     * @return ResponseEntity with transcription results in JSON, text, or SRT format
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> transcription(@Valid @ModelAttribute TranscriptionRequest request) {
        WhisperTranscribeConfig config = transcriptionMapper.toWhisperParams(request);

        List<WhisperSegment> segments =
                transcriptionService.transcribe(config, request.file().getResource());

        if (request.responseFormat() == ResponseFormat.TEXT) {
            String text = transcriptionMapper.toText(segments);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(text);
        } else if (request.responseFormat() == ResponseFormat.SRT) {
            String srt = transcriptionMapper.toSrt(segments);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(srt);
        } else {
            TranscriptionResponse response = transcriptionMapper.toJson(segments);
            return ResponseEntity.ok(response);
        }
    }
}
