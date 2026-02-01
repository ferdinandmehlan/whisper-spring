package io.github.ferdinandmehlan.whisperspring._native.callbacks;

import java.lang.foreign.MemorySegment;

/**
 * Callback interface invoked when a new transcription segment is decoded.
 */
public interface WhisperNewSegmentCallback {

    /**
     * Called when a new segment is decoded.
     *
     * @param ctx the Whisper context
     * @param state the Whisper state
     * @param nNew the number of new segments
     * @param userData user-provided data
     */
    void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData);
}
