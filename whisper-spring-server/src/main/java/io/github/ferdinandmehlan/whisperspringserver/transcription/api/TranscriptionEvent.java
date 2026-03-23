package io.github.ferdinandmehlan.whisperspringserver.transcription.api;

/**
 * Server-Sent Event payload for streaming transcription segments to clients.
 *
 * @param start start timestamp in milliseconds
 * @param end end timestamp in milliseconds
 * @param text transcribed text for this segment
 */
public record TranscriptionEvent(long start, long end, String text) {}
