package io.github.ferdinandmehlan.whisperspring._native;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperNativeConfig;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionMetadata;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperContextParams;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperFullParams;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperH;
import io.github.ferdinandmehlan.whisperspring._native.loader.NativeLibrary;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * High-level wrapper for the Whisper native library.
 * Provides Java-friendly methods for loading Whisper models and transcribing audio.
 */
public class WhisperNative extends WhisperH implements AutoCloseable {

    private final Arena arena = Arena.ofShared();
    private final NativeLibrary nativeLibrary;
    private final boolean closeNativeLibrary;
    private final MemorySegment ctx;

    /**
     * Creates a new WhisperNative instance with default context configuration.
     *
     * <p>The native library is loaded from {@code whisper.native-library-path} or
     * {@code WHISPER_NATIVE_LIBRARY_PATH}.
     *
     * @param modelPathStr path to the whisper model file
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr) throws IOException {
        this(modelPathStr, new WhisperNativeConfig(), NativeLibrary.loadDefault(), true);
    }

    /**
     * Creates a new WhisperNative instance with default context configuration and an explicit native
     * library file or directory.
     *
     * @param modelPathStr path to the whisper model file
     * @param nativeLibraryPath path to the native library file or its directory
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, String nativeLibraryPath) throws IOException {
        this(modelPathStr, new WhisperNativeConfig(), NativeLibrary.load(nativeLibraryPath), true);
    }

    /**
     * Creates a new WhisperNative instance with default context configuration and an explicit native
     * library file or directory.
     *
     * @param modelPathStr path to the whisper model file
     * @param nativeLibraryPath path to the native library file or its directory
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, Path nativeLibraryPath) throws IOException {
        this(modelPathStr, new WhisperNativeConfig(), NativeLibrary.load(nativeLibraryPath), true);
    }

    /**
     * Creates a new WhisperNative instance with custom context configuration.
     *
     * <p>The native library is loaded from {@code whisper.native-library-path} or
     * {@code WHISPER_NATIVE_LIBRARY_PATH}.
     *
     * @param modelPathStr path to the whisper model file
     * @param contextConfig configuration for context initialization
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, WhisperNativeConfig contextConfig) throws IOException {
        this(modelPathStr, contextConfig, NativeLibrary.loadDefault(), true);
    }

    /**
     * Creates a new WhisperNative instance with custom context configuration and an explicit native
     * library file or directory.
     *
     * @param modelPathStr path to the whisper model file
     * @param contextConfig configuration for context initialization
     * @param nativeLibraryPath path to the native library file or its directory
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, WhisperNativeConfig contextConfig, String nativeLibraryPath)
            throws IOException {
        this(modelPathStr, contextConfig, NativeLibrary.load(nativeLibraryPath), true);
    }

    /**
     * Creates a new WhisperNative instance with custom context configuration and an explicit native
     * library file or directory.
     *
     * @param modelPathStr path to the whisper model file
     * @param contextConfig configuration for context initialization
     * @param nativeLibraryPath path to the native library file or its directory
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, WhisperNativeConfig contextConfig, Path nativeLibraryPath)
            throws IOException {
        this(modelPathStr, contextConfig, NativeLibrary.load(nativeLibraryPath), true);
    }

    /**
     * Creates a new WhisperNative instance with default context configuration using an already loaded
     * native library. The caller owns the native library and is responsible for closing it.
     *
     * @param modelPathStr path to the whisper model file
     * @param nativeLibrary the loaded native library
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, NativeLibrary nativeLibrary) throws IOException {
        this(modelPathStr, new WhisperNativeConfig(), nativeLibrary, false);
    }

    /**
     * Creates a new WhisperNative instance using an already loaded native library. The caller owns the
     * native library and is responsible for closing it.
     *
     * @param modelPathStr path to the whisper model file
     * @param contextConfig configuration for context initialization
     * @param nativeLibrary the loaded native library
     * @throws IOException if the model cannot be loaded
     */
    public WhisperNative(String modelPathStr, WhisperNativeConfig contextConfig, NativeLibrary nativeLibrary)
            throws IOException {
        this(modelPathStr, contextConfig, nativeLibrary, false);
    }

    private WhisperNative(
            String modelPathStr,
            WhisperNativeConfig contextConfig,
            NativeLibrary nativeLibrary,
            boolean closeNativeLibrary)
            throws IOException {
        super(nativeLibrary.lookup());
        this.nativeLibrary = nativeLibrary;
        this.closeNativeLibrary = closeNativeLibrary;

        try {
            MemorySegment modelPath = arena.allocateFrom(modelPathStr);
            MemorySegment contextParams = WhisperContextParams.allocate(arena, contextConfig);
            this.ctx = initFromFileWithParams(modelPath, contextParams);
            if (ctx.equals(MemorySegment.NULL)) {
                throw new IOException("Failed to load model: " + modelPathStr);
            }
        } catch (Throwable t) {
            arena.close();
            if (closeNativeLibrary) {
                nativeLibrary.close();
            }
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
                long t0 = fullGetSegmentT0(ctx, i) * 10;
                long t1 = fullGetSegmentT1(ctx, i) * 10;
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
            if (closeNativeLibrary) {
                nativeLibrary.close();
            }
        }
    }
}
