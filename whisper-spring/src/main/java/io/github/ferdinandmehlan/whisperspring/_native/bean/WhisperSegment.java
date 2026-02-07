package io.github.ferdinandmehlan.whisperspring._native.bean;

/**
 * Represents a single segment of a Whisper transcription.
 *
 * @param start the start timestamp in milliseconds
 * @param end the end timestamp in milliseconds
 * @param text the transcribed text for this segment
 */
public record WhisperSegment(long start, long end, String text) {}
