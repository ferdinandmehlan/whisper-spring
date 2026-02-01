package io.github.ferdinandmehlan.whisperspring._native.callback;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Helper class for registering Whisper callbacks as native function pointers.
 *
 * <p>This class uses Java 25's Foreign Function and Memory API to create upcall stubs
 * that allow native C++ code to invoke Java callback implementations. Each callback
 * type has a corresponding {@link FunctionDescriptor} that describes its native
 * signature, and the {@link #register(Arena, WhisperCallback)} method dispatches
 * to the appropriate registration logic based on the callback type.</p>
 *
 * <p>Registered stubs are allocated within the provided {@link Arena} and will be
 * automatically freed when the arena is closed. This ensures proper memory management
 * and prevents leaks during transcription operations.</p>
 *
 * @see WhisperCallback
 * @see WhisperNewSegmentCallback
 * @see WhisperProgressCallback
 * @see WhisperEncoderBeginCallback
 */
public final class CallbackHelper {

    private static final Linker LINKER = Linker.nativeLinker();
    private static final FunctionDescriptor NEW_SEGMENT_DESC = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final FunctionDescriptor PROGRESS_DESC = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final FunctionDescriptor ENCODER_BEGIN_DESC =
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);

    private static final MethodHandle BOOLEAN_TO_INT;

    static {
        try {
            BOOLEAN_TO_INT = MethodHandles.publicLookup()
                    .findStatic(Boolean.class, "hashCode", MethodType.methodType(int.class, boolean.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize boolean to int converter", e);
        }
    }

    private CallbackHelper() {}

    /**
     * Registers a Whisper callback and returns a native function pointer.
     *
     * <p>This method dispatches to the appropriate registration logic based on
     * the callback type. It creates an upcall stub that allows native code to
     * invoke the Java callback implementation. The stub is allocated within
     * the provided arena and will be freed when the arena is closed.</p>
     *
     * @param arena the arena to allocate the upcall stub within
     * @param callback the callback to register, or {@code null} for no callback
     * @return a memory segment representing the native function pointer, or
     *         {@link MemorySegment#NULL} if the callback is {@code null}
     */
    public static MemorySegment register(Arena arena, WhisperCallback callback) {
        if (callback == null) return MemorySegment.NULL;
        return switch (callback) {
            case WhisperNewSegmentCallback c ->
                createStub(
                        arena,
                        c,
                        WhisperNewSegmentCallback.class,
                        MethodType.methodType(
                                void.class, MemorySegment.class, MemorySegment.class, int.class, MemorySegment.class),
                        NEW_SEGMENT_DESC);
            case WhisperProgressCallback c ->
                createStub(
                        arena,
                        c,
                        WhisperProgressCallback.class,
                        MethodType.methodType(
                                void.class, MemorySegment.class, MemorySegment.class, int.class, MemorySegment.class),
                        PROGRESS_DESC);
            case WhisperEncoderBeginCallback c ->
                createStub(
                        arena,
                        c,
                        WhisperEncoderBeginCallback.class,
                        MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class),
                        ENCODER_BEGIN_DESC);
        };
    }

    /**
     * Creates an upcall stub for a callback implementation.
     *
     * <p>This method uses {@link MethodHandles} to create a method handle for the
     * callback's {@code callback} method, binds the callback instance to the handle,
     * and then creates an upcall stub using the {@link Linker}. The resulting
     * stub can be passed to native code as a function pointer.</p>
     *
     * <p>If {@code booleanToInt} is {@code true}, the method handle's return value
     * is converted from {@code boolean} to {@code int} using {@link Boolean#hashCode(boolean)},
     * which is required for native callbacks that expect C {@code bool} (mapped to
     * {@code int} in FFM) but Java interfaces use {@code boolean} return types.</p>
     *
     * @param <T> the callback type
     * @param arena the arena to allocate the stub within
     * @param callback the callback instance to register
     * @param callbackClass the class of the callback for method lookup
     * @param methodType the method type signature of the callback method
     * @param descriptor the function descriptor describing the native signature
     * @return a memory segment containing the upcall stub
     * @throws RuntimeException if method handle creation or stub allocation fails
     */
    private static <T extends WhisperCallback> MemorySegment createStub(
            Arena arena, T callback, Class<T> callbackClass, MethodType methodType, FunctionDescriptor descriptor) {
        try {
            MethodHandle handle = MethodHandles.publicLookup().findVirtual(callbackClass, "callback", methodType);
            handle = MethodHandles.insertArguments(handle, 0, callback);
            return LINKER.upcallStub(handle, descriptor, arena);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create callback stub", e);
        }
    }
}
