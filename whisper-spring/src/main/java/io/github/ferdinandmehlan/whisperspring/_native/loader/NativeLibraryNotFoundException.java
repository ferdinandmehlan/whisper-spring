package io.github.ferdinandmehlan.whisperspring._native.loader;

public class NativeLibraryNotFoundException extends RuntimeException {

    public NativeLibraryNotFoundException(String message) {
        super(message);
    }

    public NativeLibraryNotFoundException(String libName, String engine, PlatformEnum platform) {
        super(buildMessage(libName, engine, platform));
    }

    private static String buildMessage(String libName, String engine, PlatformEnum platform) {
        StringBuilder sb = new StringBuilder();
        sb.append("Native library not found: ").append(libName);
        if (engine != null) {
            sb.append(" (").append(engine).append(")");
        }
        sb.append("\nPlatform: ").append(platform.getOsDir()).append("/").append(platform.getArchDir());
        sb.append("\nResource path: ").append(platform.getResourcePath(libName, engine));
        sb.append("\n\nPossible solutions:");
        sb.append("\n1. Download or build native libraries for your platform from:");
        sb.append("\n   https://github.com/ggerganov/whisper.cpp/releases");
        sb.append("\n2. Ensure native libraries are included in the classpath");
        sb.append("\n3. Check that the library exists at: /native/");
        return sb.toString();
    }
}
