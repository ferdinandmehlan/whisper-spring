package io.github.ferdinandmehlan.whisperspring._native.loader;

import java.nio.file.Path;

/**
 * Thrown when a configured whisper native library cannot be found or loaded.
 */
public class NativeLibraryNotFoundException extends RuntimeException {

    public NativeLibraryNotFoundException(String message) {
        super(message);
    }

    public NativeLibraryNotFoundException(Path configuredPath, NativePlatform platform) {
        super(buildMessage(configuredPath, platform));
    }

    private static String buildMessage(Path configuredPath, NativePlatform platform) {
        return """
                Native library not found: %s
                Platform: %s
                Expected a shared library file such as %s, or a directory containing it.

                Set whisper.native-library-path to the library file or its directory.
                Build whisper.cpp with -DBUILD_SHARED_LIBS=ON and provide libwhisper plus its ggml dependencies.
                """.formatted(
                        configuredPath.toAbsolutePath().normalize(),
                        platform.description(),
                        platform.libraryFileName("whisper"));
    }
}
