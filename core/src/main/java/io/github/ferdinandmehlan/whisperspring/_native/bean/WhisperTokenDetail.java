package io.github.ferdinandmehlan.whisperspring._native.bean;

/**
 * Represents a single token in a Whisper transcription segment with its probability score.
 *
 * @param token the token text
 * @param probability the probability score (0.0 to 1.0)
 */
public record WhisperTokenDetail(String token, float probability) {}
