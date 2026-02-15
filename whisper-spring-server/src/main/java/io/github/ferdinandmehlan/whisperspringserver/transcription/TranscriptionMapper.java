package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import io.github.ferdinandmehlan.whisperspringserver.WhisperServerConfiguration;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between transcription request/response objects and Whisper parameters.
 * Handles transformation of data between different formats for transcription processing.
 */
@Component
public class TranscriptionMapper {

    private final WhisperServerConfiguration configuration;

    /**
     * Creates a new TranscriptionMapper with server configuration.
     *
     * @param configuration the server configuration containing thread settings
     */
    public TranscriptionMapper(WhisperServerConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Converts an TranscriptionRequest to WhisperParams for transcription.
     *
     * @param request the transcription request with transcription parameters
     * @return WhisperParams configured for transcription
     */
    public WhisperTranscribeConfig toWhisperParams(TranscriptionRequest request) {
        WhisperTranscribeConfig config = new WhisperTranscribeConfig();
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
        config.nThreads = configuration.getThreads();
        return config;
    }

    /**
     * Converts whisper segments to plain text format.
     *
     * @param segments the list of whisper segments
     * @return concatenated text from all segments
     */
    public String toText(List<WhisperSegment> segments) {
        StringBuilder text = new StringBuilder();
        for (WhisperSegment segment : segments) {
            text.append(segment.text().trim());
        }
        return text.toString();
    }

    /**
     * Converts whisper segments to SRT (SubRip) subtitle format.
     *
     * @param segments the list of whisper segments
     * @return SRT formatted string with timestamps and text
     */
    public String toSrt(List<WhisperSegment> segments) {
        StringBuilder srt = new StringBuilder();
        int index = 1;
        for (WhisperSegment segment : segments) {
            long start = segment.start();
            long end = segment.end();
            String startTime = formatTime(start);
            String endTime = formatTime(end);
            srt.append(index).append("\n");
            srt.append(startTime).append(" --> ").append(endTime).append("\n");
            srt.append(segment.text().trim()).append("\n\n");
            index++;
        }
        return srt.toString();
    }

    /**
     * Converts whisper segments to JSON response format.
     *
     * @param segments the list of whisper segments
     * @return TranscriptionResponse with full text and segment details
     */
    public TranscriptionResponse toJson(List<WhisperSegment> segments) {
        String text = toText(segments);
        List<WhisperSegment> trimmedSegments = segments.stream()
                .map(segment -> new WhisperSegment(
                        segment.start(), segment.end(), segment.text().trim()))
                .collect(Collectors.toList());
        return new TranscriptionResponse(text, trimmedSegments);
    }

    private String formatTime(long whisperTime) {
        long millis = whisperTime * 10;
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;
        long ms = millis % 1000;
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, ms);
    }
}
