package io.github.ferdinandmehlan.whisperspring._native.bean;

/**
 * Configuration for Whisper context initialization.
 * Controls GPU usage, flash attention, and device selection.
 */
public class WhisperNativeConfig {

    public boolean useGpu;
    public boolean flashAttn;
    public int gpuDevice;

    /**
     * Creates a new WhisperNativeConfig with default settings.
     */
    public WhisperNativeConfig() {
        this.useGpu = true;
        this.flashAttn = true;
        this.gpuDevice = 0;
    }
}
