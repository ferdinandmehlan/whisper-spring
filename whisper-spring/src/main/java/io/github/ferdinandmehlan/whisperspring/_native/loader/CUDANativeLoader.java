package io.github.ferdinandmehlan.whisperspring._native.loader;

public final class CUDANativeLoader extends NativeLoader {
    private volatile boolean loaded = false;

    @Override
    public synchronized void loadLibraries() {
        if (loaded) {
            return;
        }
        loadLibrary("ggml-base", "cuda");
        loadLibrary("ggml-cpu", "cuda");
        loadLibrary("ggml-cuda", "cuda");
        loadLibrary("ggml", "cuda");
        loadLibrary("whisper", "cuda");
        loaded = true;
    }
}
