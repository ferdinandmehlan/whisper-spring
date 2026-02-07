package io.github.ferdinandmehlan.whisperspring._native.callbacks;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

/**
 * Registry for managing native callback registrations.
 * Handles conversion of Java callbacks to native function pointers.
 */
public class CallbackRegistry {

    private static final Linker LINKER = Linker.nativeLinker();

    private static final FunctionDescriptor NEW_SEGMENT_DESC = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);

    private static final FunctionDescriptor PROGRESS_DESC = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);

    private static final FunctionDescriptor ENCODER_BEGIN_DESC =
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);

    private final List<MemorySegment> upcallStubs = new ArrayList<>();
    private final Arena upcallArena;

    /**
     * Creates a new CallbackRegistry.
     *
     * @param upcallArena the arena for upcall memory management
     */
    public CallbackRegistry(Arena upcallArena) {
        this.upcallArena = upcallArena;
    }

    /**
     * Registers a new segment callback.
     *
     * @param callback the callback to register
     * @return memory segment containing the native callback pointer
     */
    public MemorySegment registerNewSegmentCallback(WhisperNewSegmentCallback callback) {
        if (callback == null) {
            return MemorySegment.NULL;
        }

        try {
            MethodHandle handle = MethodHandles.publicLookup()
                    .findVirtual(
                            WhisperNewSegmentCallback.class,
                            "callback",
                            MethodType.methodType(
                                    void.class,
                                    MemorySegment.class,
                                    MemorySegment.class,
                                    int.class,
                                    MemorySegment.class));
            handle = MethodHandles.insertArguments(handle, 0, callback);
            handle = handle.asType(MethodType.methodType(
                    void.class, MemorySegment.class, MemorySegment.class, int.class, MemorySegment.class));

            MemorySegment stub = LINKER.upcallStub(handle, NEW_SEGMENT_DESC, upcallArena);
            upcallStubs.add(stub);
            return stub;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register new segment callback", e);
        }
    }

    /**
     * Registers a progress callback.
     *
     * @param callback the callback to register
     * @return memory segment containing the native callback pointer
     */
    public MemorySegment registerProgressCallback(WhisperProgressCallback callback) {
        if (callback == null) {
            return MemorySegment.NULL;
        }

        try {
            MethodHandle handle = MethodHandles.publicLookup()
                    .findVirtual(
                            WhisperProgressCallback.class,
                            "callback",
                            MethodType.methodType(
                                    void.class,
                                    MemorySegment.class,
                                    MemorySegment.class,
                                    int.class,
                                    MemorySegment.class));
            handle = MethodHandles.insertArguments(handle, 0, callback);
            handle = handle.asType(MethodType.methodType(
                    void.class, MemorySegment.class, MemorySegment.class, int.class, MemorySegment.class));

            MemorySegment stub = LINKER.upcallStub(handle, PROGRESS_DESC, upcallArena);
            upcallStubs.add(stub);
            return stub;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register progress callback", e);
        }
    }

    /**
     * Registers an encoder begin callback.
     *
     * @param callback the callback to register
     * @return memory segment containing the native callback pointer
     */
    public MemorySegment registerEncoderBeginCallback(WhisperEncoderBeginCallback callback) {
        if (callback == null) {
            return MemorySegment.NULL;
        }

        try {
            MethodHandle handle = MethodHandles.publicLookup()
                    .findVirtual(
                            WhisperEncoderBeginCallback.class,
                            "callback",
                            MethodType.methodType(
                                    boolean.class, MemorySegment.class, MemorySegment.class, MemorySegment.class));
            handle = MethodHandles.insertArguments(handle, 0, callback);
            handle = handle.asType(
                    MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class));

            MemorySegment stub = LINKER.upcallStub(handle, ENCODER_BEGIN_DESC, upcallArena);
            upcallStubs.add(stub);
            return stub;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register encoder begin callback", e);
        }
    }

    /**
     * Releases resources held by this CallbackRegistry.
     */
    public void close() {
        upcallArena.close();
        upcallStubs.clear();
    }
}
