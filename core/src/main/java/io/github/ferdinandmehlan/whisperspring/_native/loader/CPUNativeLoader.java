package io.github.ferdinandmehlan.whisperspring._native.loader;

public final class CPUNativeLoader extends NativeLoader {
    private volatile boolean loaded = false;

    @Override
    public synchronized void loadLibraries() {
        if (loaded) {
            return;
        }
        loadLibrary("ggml-base", null);
        loadLibrary("ggml-cpu", null);
        loadLibrary("ggml", null);
        loadLibrary("whisper", null);
        loaded = true;
    }
}
