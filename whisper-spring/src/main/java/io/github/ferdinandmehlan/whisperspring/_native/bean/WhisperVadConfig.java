package io.github.ferdinandmehlan.whisperspring._native.bean;

/**
 * Configuration for Voice Activity Detection (VAD).
 */
public class WhisperVadConfig {
    public float threshold;
    public int minSpeechDurationMs;
    public int minSilenceDurationMs;
    public float maxSpeechDurationS;
    public int speechPadMs;
    public float samplesOverlap;

    /**
     * Creates a new WhisperVadConfig with default settings.
     */
    public WhisperVadConfig() {
        this.threshold = 0.5f;
        this.minSpeechDurationMs = 250;
        this.minSilenceDurationMs = 100;
        this.maxSpeechDurationS = Float.MAX_VALUE;
        this.speechPadMs = 30;
        this.samplesOverlap = 0.1f;
    }
}
