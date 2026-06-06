package io.github.ferdinandmehlan.whisperspring._native.bean;

import java.util.List;
import org.springframework.ai.audio.transcription.AudioTranscriptionMetadata;

public class WhisperTranscriptionMetadata implements AudioTranscriptionMetadata {

    static WhisperTranscriptionMetadata NULL = new WhisperTranscriptionMetadata();

    private final List<WhisperSegment> segments;

    public WhisperTranscriptionMetadata() {
        this(List.of());
    }

    public WhisperTranscriptionMetadata(List<WhisperSegment> segments) {
        this.segments = segments != null ? List.copyOf(segments) : List.of();
    }

    public List<WhisperSegment> getSegments() {
        return this.segments;
    }
}
