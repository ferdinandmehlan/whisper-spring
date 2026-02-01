package io.github.ferdinandmehlan.whisperspring._native.ffm;

import io.github.ferdinandmehlan.whisperspring._native.loader.NativeLoader;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Base class for Whisper native library interaction.
 * Provides low-level bindings to the whisper.h C API using Panama Foreign Function Memory.
 */
public abstract class WhisperH {

    protected final Arena arena;
    protected final SymbolLookup lookup;
    protected final Linker linker;

    protected MethodHandle mhInitFromFile;
    protected MethodHandle mhInitFromFileWithParams;
    protected MethodHandle mhInitFromBufferWithParams;
    protected MethodHandle mhFree;
    protected MethodHandle mhFreeParams;
    protected MethodHandle mhFull;
    protected MethodHandle mhFullNSegments;
    protected MethodHandle mhFullGetSegmentText;
    protected MethodHandle mhFullGetSegmentT0;
    protected MethodHandle mhFullGetSegmentT1;
    protected MethodHandle mhFullDefaultParamsByRef;
    protected MethodHandle mhTokenEot;
    protected MethodHandle mhFullNTokens;
    protected MethodHandle mhFullGetTokenText;
    protected MethodHandle mhFullGetTokenId;
    protected MethodHandle mhFullGetTokenP;

    protected WhisperH() {
        this.arena = Arena.ofShared();
        this.lookup = SymbolLookup.libraryLookup(NativeLoader.getWhisperLibPath(), arena);
        this.linker = Linker.nativeLinker();
        initMethodHandles();
    }

    private void initMethodHandles() {
        mhInitFromFile = downcallHandle(
                "whisper_init_from_file", FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
        mhInitFromFileWithParams = downcallHandle(
                "whisper_init_from_file_with_params",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, WhisperContextParams.LAYOUT));
        mhInitFromBufferWithParams = downcallHandle(
                "whisper_init_from_buffer_with_params",
                FunctionDescriptor.of(
                        ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, WhisperContextParams.LAYOUT));
        mhFree = downcallHandle("whisper_free", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
        mhFreeParams = downcallHandle("whisper_free_params", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
        mhFull = downcallHandle(
                "whisper_full",
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS,
                        WhisperFullParams.LAYOUT,
                        ValueLayout.ADDRESS,
                        ValueLayout.JAVA_INT));
        mhFullNSegments = downcallHandle(
                "whisper_full_n_segments", FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
        mhFullGetSegmentText = downcallHandle(
                "whisper_full_get_segment_text",
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
        mhFullGetSegmentT0 = downcallHandle(
                "whisper_full_get_segment_t0",
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
        mhFullGetSegmentT1 = downcallHandle(
                "whisper_full_get_segment_t1",
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
        mhFullDefaultParamsByRef = downcallHandle(
                "whisper_full_default_params_by_ref", FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
        mhTokenEot =
                downcallHandle("whisper_token_eot", FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
        mhFullNTokens = downcallHandle(
                "whisper_full_n_tokens",
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
        mhFullGetTokenText = downcallHandle(
                "whisper_full_get_token_text",
                FunctionDescriptor.of(
                        ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
        mhFullGetTokenId = downcallHandle(
                "whisper_full_get_token_id",
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
        mhFullGetTokenP = downcallHandle(
                "whisper_full_get_token_p",
                FunctionDescriptor.of(
                        ValueLayout.JAVA_FLOAT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
    }

    private MethodHandle downcallHandle(String symbol, FunctionDescriptor descriptor) {
        return linker.downcallHandle(lookup.findOrThrow(symbol), descriptor);
    }

    public MemorySegment initFromFile(MemorySegment path) throws Throwable {
        return (MemorySegment) mhInitFromFile.invokeExact(path);
    }

    public MemorySegment initFromFileWithParams(MemorySegment path, MemorySegment params) throws Throwable {
        return (MemorySegment) mhInitFromFileWithParams.invokeExact(path, params);
    }

    public MemorySegment initFromBufferWithParams(MemorySegment buffer, long size, MemorySegment params)
            throws Throwable {
        return (MemorySegment) mhInitFromBufferWithParams.invokeExact(buffer, size, params);
    }

    public void free(MemorySegment ctx) throws Throwable {
        mhFree.invokeExact(ctx);
    }

    public void freeParams(MemorySegment params) throws Throwable {
        mhFreeParams.invokeExact(params);
    }

    public int full(MemorySegment ctx, MemorySegment params, MemorySegment audioData, int nSamples) throws Throwable {
        return (int) mhFull.invokeExact(ctx, params, audioData, nSamples);
    }

    public int fullNSegments(MemorySegment ctx) throws Throwable {
        return (int) mhFullNSegments.invokeExact(ctx);
    }

    public MemorySegment fullGetSegmentText(MemorySegment ctx, int iSegment) throws Throwable {
        return (MemorySegment) mhFullGetSegmentText.invokeExact(ctx, iSegment);
    }

    public long fullGetSegmentT0(MemorySegment ctx, int iSegment) throws Throwable {
        return (long) mhFullGetSegmentT0.invokeExact(ctx, iSegment);
    }

    public long fullGetSegmentT1(MemorySegment ctx, int iSegment) throws Throwable {
        return (long) mhFullGetSegmentT1.invokeExact(ctx, iSegment);
    }

    public MemorySegment fullDefaultParamsByRef(int i) throws Throwable {
        return (MemorySegment) mhFullDefaultParamsByRef.invokeExact(i);
    }

    public int tokenEot(MemorySegment ctx) throws Throwable {
        return (int) mhTokenEot.invokeExact(ctx);
    }

    public int fullNTokens(MemorySegment ctx, int iSegment) throws Throwable {
        return (int) mhFullNTokens.invokeExact(ctx, iSegment);
    }

    public MemorySegment fullGetTokenText(MemorySegment ctx, int iSegment, int iToken) throws Throwable {
        return (MemorySegment) mhFullGetTokenText.invokeExact(ctx, iSegment, iToken);
    }

    public int fullGetTokenId(MemorySegment ctx, int iSegment, int iToken) throws Throwable {
        return (int) mhFullGetTokenId.invokeExact(ctx, iSegment, iToken);
    }

    public float fullGetTokenP(MemorySegment ctx, int iSegment, int iToken) throws Throwable {
        return (float) mhFullGetTokenP.invokeExact(ctx, iSegment, iToken);
    }
}
