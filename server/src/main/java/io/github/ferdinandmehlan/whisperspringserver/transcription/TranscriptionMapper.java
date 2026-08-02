package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionRequest;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between transcription request/response objects and Whisper parameters.
 * Handles transformation of data between different formats for transcription processing.
 */
@Component
public class TranscriptionMapper {

    /**
     * Converts an TranscriptionRequest to WhisperParams for transcription.
     *
     * @param request the transcription request with transcription parameters
     * @return WhisperParams configured for transcription
     */
    public WhisperTranscriptionOptions toWhisperParams(TranscriptionRequest request) {
        WhisperTranscriptionOptions config = new WhisperTranscriptionOptions();
        config.language = request.language();
        config.translate = request.translate();
        config.initialPrompt = request.prompt();
        config.temperature = request.temperature();
        config.temperatureInc = request.temperatureInc();
        config.offsetMs = request.offsetTMs();
        config.durationMs = request.durationMs();
        config.nMaxTextCtx = request.maxContext() == -1 ? 16384 : request.maxContext();
        config.maxLen = request.maxLen();
        config.splitOnWord = request.splitOnWord();
        config.greedyBestOf = request.bestOf();
        config.beamSize = request.beamSize();
        config.audioCtx = request.audioContext();
        config.tholdPt = request.wordThreshold();
        config.entropyThold = request.entropyThreshold();
        config.logprobThold = request.logprobThreshold();
        config.noTimestamps = request.noTimestamps();
        return config;
    }

    /**
     * Converts whisper segments to JSON response format.
     *
     * @param transcription the completed whisper transcription
     * @return TranscriptionResponse with full text and segment details
     */
    public TranscriptionResponse toJson(WhisperTranscription transcription) {
        String text = transcription.getOutput();
        List<WhisperSegment> trimmedSegments = transcription.getMetadata().getSegments().stream()
                .map(segment -> new WhisperSegment(segment.text().trim(), segment.start(), segment.end()))
                .collect(Collectors.toList());
        return new TranscriptionResponse(text, trimmedSegments);
    }
}
