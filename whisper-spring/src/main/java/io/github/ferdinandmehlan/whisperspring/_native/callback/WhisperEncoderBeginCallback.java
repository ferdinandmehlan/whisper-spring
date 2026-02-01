package io.github.ferdinandmehlan.whisperspring._native.callback;

import java.lang.foreign.MemorySegment;

/**
 * Callback invoked before the encoder begins processing audio.
 *
 * <p>This callback is called before each encoder iteration, allowing
 * the application to inspect or modify state before encoding begins.
 * Return {@code non-zero} to continue, {@code 0} to abort.</p>
 *
 * @see CallbackHelper#register(java.lang.foreign.Arena, WhisperCallback)
 */
public non-sealed interface WhisperEncoderBeginCallback extends WhisperCallback {

    /**
     * Called before the encoder begins processing.
     *
     * @param ctx the Whisper context memory segment
     * @param state the Whisper state memory segment
     * @param userData user-provided data passed through from registration
     * @return {@code true} to continue encoding, {@code false} to abort
     */
    int callback(MemorySegment ctx, MemorySegment state, MemorySegment userData);
}
