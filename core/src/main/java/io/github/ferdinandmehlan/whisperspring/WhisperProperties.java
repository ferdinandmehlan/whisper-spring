package io.github.ferdinandmehlan.whisperspring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whisper")
public class WhisperProperties {

    private String modelPath;
    private String nativeLibraryPath;

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getNativeLibraryPath() {
        return nativeLibraryPath;
    }

    public void setNativeLibraryPath(String nativeLibraryPath) {
        this.nativeLibraryPath = nativeLibraryPath;
    }
}
