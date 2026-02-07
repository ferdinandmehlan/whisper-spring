package io.github.ferdinandmehlan.whisperspring._native.callbacks;

import java.lang.foreign.MemorySegment;

/**
 * Callback interface invoked when the encoder begins processing.
 */
public interface WhisperEncoderBeginCallback {

    /**
     * Called when the encoder begins.
     *
     * @param ctx the Whisper context
     * @param state the Whisper state
     * @param userData user-provided data
     * @return 0 to continue, non-zero to abort
     */
    boolean callback(MemorySegment ctx, MemorySegment state, MemorySegment userData);
}
