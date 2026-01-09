package io.github.ferdinandmehlan.whisperspringserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Whisper Spring server.
 * Maps application properties with the "whisper" prefix to configuration fields.
 */
@Component
@ConfigurationProperties(prefix = "whisper")
public class WhisperServerConfiguration {

    private String model;
    private int threads;
    private boolean noGpu;
    private boolean flashAttn;

    // Getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public boolean isNoGpu() {
        return noGpu;
    }

    public void setNoGpu(boolean noGpu) {
        this.noGpu = noGpu;
    }

    public boolean isFlashAttn() {
        return flashAttn;
    }

    public void setFlashAttn(boolean flashAttn) {
        this.flashAttn = flashAttn;
    }
}
