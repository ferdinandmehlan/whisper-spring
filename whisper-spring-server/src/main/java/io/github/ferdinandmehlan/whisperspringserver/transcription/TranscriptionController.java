package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

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
     * @return TranscriptionResponse with transcription results
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, params = "stream=false")
    public TranscriptionResponse transcription(@Valid @ModelAttribute TranscriptionRequest request) {
        WhisperTranscribeConfig config = transcriptionMapper.toWhisperParams(request);
        List<WhisperSegment> segments =
                transcriptionService.transcribe(config, request.file().getResource());
        return transcriptionMapper.toJson(segments);
    }

    /**
     * Handles audio transcription requests.
     * Accepts multipart form data with audio file and transcription parameters,
     * returns a stream of partial transcription results.
     *
     * @param request the transcription request containing audio file and parameters
     * @return Flux<ServerSentEvent<WhisperSegment>> with partial transcription results
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, params = "stream=true")
    public Flux<ServerSentEvent<WhisperSegment>> transcriptionStream(@Valid @ModelAttribute TranscriptionRequest request) {
        WhisperTranscribeConfig config = transcriptionMapper.toWhisperParams(request);
        Sinks.Many<ServerSentEvent<WhisperSegment>> sink = Sinks.many().unicast().onBackpressureBuffer();
        config.newSegmentCallback = new NewSegmentCallback(transcriptionService.getWhisper(), sink);

        Thread.ofVirtual().start(() -> {
            try {
                transcriptionService.transcribe(config, request.file().getResource());
                sink.tryEmitComplete();
            } catch (Exception e) {
                sink.tryEmitError(e);
            }
        });

        return sink.asFlux().onTerminateDetach();
    }
}
