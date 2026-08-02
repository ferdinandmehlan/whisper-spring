package io.github.ferdinandmehlan.whisperspring._native.callback;

import java.lang.foreign.MemorySegment;

/**
 * Callback invoked periodically during transcription to report progress.
 *
 * <p>This callback is called at regular intervals during the encoding and
 * decoding phases of whisper processing. It can be used to update progress
 * indicators or implement real-time transcription display.</p>
 *
 * @see CallbackHelper#register(java.lang.foreign.Arena, WhisperCallback)
 */
public non-sealed interface WhisperProgressCallback extends WhisperCallback {

    /**
     * Called to report transcription progress.
     *
     * @param ctx the Whisper context memory segment
     * @param state the Whisper state memory segment
     * @param progress the progress percentage (0-100)
     * @param userData user-provided data passed through from registration
     */
    void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData);
}
