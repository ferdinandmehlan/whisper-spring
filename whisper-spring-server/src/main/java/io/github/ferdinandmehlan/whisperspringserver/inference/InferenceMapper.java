package io.github.ferdinandmehlan.whisperspringserver.inference;

import io.github.ferdinandmehlan.whisperspring.WhisperParams;
import io.github.ferdinandmehlan.whisperspringserver.WhisperServerConfiguration;
import io.github.ggerganov.whispercpp.bean.WhisperSegment;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between inference request/response objects and Whisper parameters.
 * Handles transformation of data between different formats for transcription processing.
 */
@Component
public class InferenceMapper {

    private final WhisperServerConfiguration configuration;

    /**
     * Creates a new InferenceMapper with server configuration.
     *
     * @param configuration the server configuration containing thread settings
     */
    public InferenceMapper(WhisperServerConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Converts an InferenceRequest to WhisperParams for transcription.
     *
     * @param request the inference request with transcription parameters
     * @return WhisperParams configured for transcription
     */
    public WhisperParams toWhisperParams(InferenceRequest request) {
        return new WhisperParams(
                request.language(),
                request.translate(),
                request.prompt(),
                request.temperature(),
                request.temperatureInc(),
                request.offsetTMs(),
                request.offsetN(),
                request.durationMs(),
                request.maxContext(),
                request.maxLen(),
                request.splitOnWord(),
                request.bestOf(),
                request.beamSize(),
                request.audioContext(),
                request.wordThreshold(),
                request.entropyThreshold(),
                request.logprobThreshold(),
                request.noTimestamps(),
                configuration.getThreads(),
                null,
                null);
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
            text.append(segment.getSentence().trim());
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
            long start = segment.getStart();
            long end = segment.getEnd();
            String startTime = formatTime(start);
            String endTime = formatTime(end);
            srt.append(index).append("\n");
            srt.append(startTime).append(" --> ").append(endTime).append("\n");
            srt.append(segment.getSentence().trim()).append("\n\n");
            index++;
        }
        return srt.toString();
    }

    /**
     * Converts whisper segments to JSON response format.
     *
     * @param segments the list of whisper segments
     * @return InferenceResponse with full text and segment details
     */
    public InferenceResponse toJson(List<WhisperSegment> segments) {
        String text = toText(segments);
        List<WhisperSegment> trimmedSegments = segments.stream()
                .map(segment -> new WhisperSegment(
                        segment.getStart(),
                        segment.getEnd(),
                        segment.getSentence().trim()))
                .collect(Collectors.toList());
        return new InferenceResponse(text, trimmedSegments);
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
