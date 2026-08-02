package io.github.ferdinandmehlan.whisperspring._native.callback;

import java.lang.foreign.MemorySegment;

/**
 * Callback invoked when one or more new transcription segments are decoded.
 *
 * <p>This callback is triggered during {@code whisper_full} processing whenever
 * the model produces new text segments. It can be used to stream transcription
 * results in real-time as they become available.</p>
 *
 * @see CallbackHelper#register(java.lang.foreign.Arena, WhisperCallback)
 */
public non-sealed interface WhisperNewSegmentCallback extends WhisperCallback {

    /**
     * Called when new segment(s) are decoded.
     *
     * @param ctx the Whisper context memory segment
     * @param state the Whisper state memory segment
     * @param nNew the number of new segments decoded in this call
     * @param userData user-provided data passed through from registration
     */
    void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData);
}
