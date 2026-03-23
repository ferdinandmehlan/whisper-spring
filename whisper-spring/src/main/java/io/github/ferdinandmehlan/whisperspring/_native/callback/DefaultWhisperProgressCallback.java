package io.github.ferdinandmehlan.whisperspring._native.callback;

import java.lang.foreign.MemorySegment;

/**
 * Abstract callback implementation for handling Whisper transcription progress updates.
 * Normalizes progress values to a 0-100 range.
 */
public abstract class DefaultWhisperProgressCallback implements WhisperProgressCallback {

    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData) {
        progress = Math.min(progress, 100);
        handle(progress);
    }

    /**
     * Called with transcription progress updates (0-100).
     *
     * @param progress the progress percentage (0-100)
     */
    public abstract void handle(int progress);
}
