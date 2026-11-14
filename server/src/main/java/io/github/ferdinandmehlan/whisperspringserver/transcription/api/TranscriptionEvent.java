package io.github.ferdinandmehlan.whisperspringserver.transcription.api;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;

/**
 * Server-Sent Event payload for streaming transcription segments to clients.
 *
 * @param start start timestamp in milliseconds
 * @param end end timestamp in milliseconds
 * @param text transcribed text for this segment
 */
public record TranscriptionEvent(long start, long end, String text) {

    /**
     * Creates an event from a segment decoded by Whisper.
     *
     * @param segment the decoded segment
     * @return the event carrying the segment's timestamps and trimmed text
     */
    public static TranscriptionEvent from(WhisperSegment segment) {
        return new TranscriptionEvent(
                segment.start(), segment.end(), segment.text().trim());
    }
}
