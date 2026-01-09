package io.github.ferdinandmehlan.whisperspring;

import io.github.ggerganov.whispercpp.callbacks.WhisperNewSegmentCallback;
import io.github.ggerganov.whispercpp.callbacks.WhisperProgressCallback;

/**
 * Record containing parameters for Whisper transcription.
 * This encapsulates all the configuration options that can be passed to the Whisper engine.
 */
public record WhisperParams(
        String language,
        boolean translate,
        String prompt,
        float temperature,
        float temperatureInc,
        int offsetTMs,
        int offsetN,
        int durationMs,
        int maxContext,
        int maxLen,
        boolean splitOnWord,
        int bestOf,
        int beamSize,
        int audioContext,
        float wordThreshold,
        float entropyThreshold,
        float logprobThreshold,
        boolean noTimestamps,
        int threads,
        WhisperNewSegmentCallback whisperNewSegmentCallback,
        WhisperProgressCallback whisperProgressCallback) {
    public WhisperParams() {
        // @formatter:off spotless:off
        this(
            "auto",
            false,
            null,
            0.0f,
            0.2f,
            2,
            -1,
            0,
            -1,
            0,
            false,
            5,
            5,
            0,
            0.01f,
            2.4f,
            -1.0f,
            false,
            4,
            null,
            null
        );
        // @formatter:on spotless:on
    }
}
