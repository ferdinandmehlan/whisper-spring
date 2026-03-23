package io.github.ferdinandmehlan.whisperspring._native.bean;

import java.util.List;

/**
 * Represents a single segment of a Whisper transcription.
 *
 * @param text  the transcribed text for this segment
 * @param start the start timestamp in milliseconds
 * @param end   the end timestamp in milliseconds
 */
public record WhisperSegment(String text, long start, long end, List<WhisperTokenDetail> tokens) {

    public WhisperSegment(String text, long start, long end) {
        this(text, start, end, List.of());
    }
}
