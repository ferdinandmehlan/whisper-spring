package io.github.ferdinandmehlan.whisperspring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whisper")
public class WhisperProperties {

    private String modelPath;
    private boolean noGpu = false;
    private boolean flashAttn = true;
    private int gpuDevice = 0;

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
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

    public int getGpuDevice() {
        return gpuDevice;
    }

    public void setGpuDevice(int gpuDevice) {
        this.gpuDevice = gpuDevice;
    }
}
