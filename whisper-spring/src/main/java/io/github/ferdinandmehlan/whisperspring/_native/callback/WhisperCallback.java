package io.github.ferdinandmehlan.whisperspring._native.callback;

/**
 * Sealed interface for all Whisper callback types.
 * Used by {@link CallbackHelper}
 * to register native callbacks from Java.
 */
public sealed interface WhisperCallback
        permits WhisperNewSegmentCallback, WhisperProgressCallback, WhisperEncoderBeginCallback {}
