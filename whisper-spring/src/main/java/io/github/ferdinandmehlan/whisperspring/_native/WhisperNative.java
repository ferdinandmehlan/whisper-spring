package io.github.ferdinandmehlan.whisperspring._native;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperNativeConfig;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionMetadata;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperContextParams;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperFullParams;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperH;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this(modelPathStr, new WhisperNativeConfig());
    }

    /**
     * Creates a new WhisperNative instance with custom context configuration.
     *
     * @param modelPathStr path to the whisper model file
     * @param contextConfig configuration for context initialization
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, WhisperNativeConfig contextConfig) throws IOException {
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
     * Transcribes audio data with custom transcription configuration.
     *
     * @param audioData array of float audio samples
     * @param transcriptionOptions configuration for transcription
     * @return list of transcription segments
     */
    public WhisperTranscription transcribe(float[] audioData, WhisperTranscriptionOptions transcriptionOptions) {

        try (Arena callArena = Arena.ofConfined()) {
            MemorySegment audioSegment = callArena.allocateFrom(ValueLayout.JAVA_FLOAT, audioData);
            MemorySegment params = WhisperFullParams.allocate(callArena, transcriptionOptions);

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
                segments.add(new WhisperSegment(text, t0, t1));
            }

            String fullTranscriptText = segments.stream()
                    .map(WhisperSegment::text)
                    .filter(text -> text != null && !text.isBlank())
                    .collect(Collectors.joining("\n"));

            return new WhisperTranscription(fullTranscriptText)
                    .withTranscriptionMetadata(new WhisperTranscriptionMetadata(segments));
        } catch (Throwable t) {
            throw new RuntimeException("Failed to transcribe", t);
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
