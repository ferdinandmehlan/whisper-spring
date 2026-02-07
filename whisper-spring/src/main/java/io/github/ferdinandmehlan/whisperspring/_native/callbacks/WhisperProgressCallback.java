package io.github.ferdinandmehlan.whisperspring._native.callbacks;

import java.lang.foreign.MemorySegment;

/**
 * Callback interface invoked during transcription progress.
 */
public interface WhisperProgressCallback {

    /**
     * Called during transcription to report progress.
     *
     * @param ctx the Whisper context
     * @param state the Whisper state
     * @param progress the progress percentage (0-100)
     * @param userData user-provided data
     */
    void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData);
}
