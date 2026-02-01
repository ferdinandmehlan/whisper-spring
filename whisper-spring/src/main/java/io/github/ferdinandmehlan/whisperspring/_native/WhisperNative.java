package io.github.ferdinandmehlan.whisperspring._native;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperContextConfig;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperContextParams;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperFullParams;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperH;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * High-level wrapper for the Whisper native library.
 * Provides Java-friendly methods for loading Whisper models and transcribing audio.
 */
public class WhisperNative extends WhisperH implements AutoCloseable {

    private final MemorySegment ctx;

    /**
     * Creates a new WhisperNative instance with default context configuration.
     *
     * @param modelPathStr path to the whisper model file
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr) throws IOException {
        this(modelPathStr, new WhisperContextConfig());
    }

    /**
     * Creates a new WhisperNative instance with custom context configuration.
     *
     * @param modelPathStr path to the whisper model file
     * @param contextConfig configuration for context initialization
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, WhisperContextConfig contextConfig) throws IOException {
        super();

        try {
            MemorySegment modelPath = arena.allocateFrom(modelPathStr);
            MemorySegment contextParams = WhisperContextParams.allocate(arena, contextConfig);
            this.ctx = initFromFileWithParams(modelPath, contextParams);
            if (ctx.equals(MemorySegment.NULL)) {
                throw new IOException("Failed to load model: " + modelPathStr);
            }
        } catch (Throwable t) {
            arena.close();
            throw new IOException("Failed to initialize WhisperNative", t);
        }
    }

    /**
     * Transcribes audio data using default transcription configuration.
     *
     * @param audioData array of float audio samples
     * @return list of transcription segments
     * @throws IOException if transcription fails
     */
    public List<WhisperSegment> transcribe(float[] audioData) throws IOException {
        return transcribe(audioData, new WhisperTranscribeConfig());
    }

    /**
     * Transcribes audio data with custom transcription configuration.
     *
     * @param audioData array of float audio samples
     * @param transcribeConfig configuration for transcription
     * @return list of transcription segments
     * @throws IOException if transcription fails
     */
    public List<WhisperSegment> transcribe(float[] audioData, WhisperTranscribeConfig transcribeConfig)
            throws IOException {

        try (Arena callArena = Arena.ofConfined()) {
            MemorySegment audioSegment = callArena.allocateFrom(ValueLayout.JAVA_FLOAT, audioData);
            MemorySegment params = WhisperFullParams.allocate(callArena, transcribeConfig);

            int result = full(ctx, params, audioSegment, audioData.length);
            if (result != 0) {
                throw new IOException("Failed to process audio");
            }

            int nSegments = fullNSegments(ctx);
            List<WhisperSegment> segments = new ArrayList<>(nSegments);

            for (int i = 0; i < nSegments; i++) {
                long t0 = fullGetSegmentT0(ctx, i);
                long t1 = fullGetSegmentT1(ctx, i);
                MemorySegment textSegment = fullGetSegmentText(ctx, i);
                String text = textSegment.reinterpret(1000).getString(0);
                segments.add(new WhisperSegment(t0, t1, text));
            }

            return segments;
        } catch (Throwable t) {
            throw new IOException("Failed to transcribe", t);
        }
    }

    /**
     * Releases resources held by this WhisperNative instance.
     */
    @Override
    public void close() {
        try {
            if (!ctx.equals(MemorySegment.NULL)) {
                free(ctx);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Failed to free resources", t);
        } finally {
            arena.close();
        }
    }
}
