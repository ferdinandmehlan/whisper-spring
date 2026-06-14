package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperNativeConfig;
import java.io.IOException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Autoconfiguration class for Whisper Spring integration.
 * Provides default beans for WaveService and WhisperTranscriptionModel
 * if they are not already defined in the application context.
 * When {@code whisper.model-path} is configured, a {@link WhisperNative}
 * bean is also created and wired into the transcription model.
 */
@Configuration
@EnableConfigurationProperties(WhisperProperties.class)
public class WhisperAutoConfiguration {

    /**
     * Provides a default WaveService bean if none is defined.
     *
     * @return a new WaveService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public WaveService waveService() {
        return new WaveService();
    }

    /**
     * Creates a {@link WhisperNative} bean when {@code whisper.model-path} is set.
     *
     * @param properties the whisper configuration properties
     * @return a new WhisperNative instance
     * @throws IOException if the model cannot be loaded
     */
    @Bean
    @ConditionalOnProperty(prefix = "whisper", name = "model-path")
    @ConditionalOnMissingBean
    public WhisperNative whisperNative(WhisperProperties properties) throws IOException {
        WhisperNativeConfig config = new WhisperNativeConfig();
        config.useGpu = !properties.isNoGpu();
        config.flashAttn = properties.isFlashAttn();
        config.gpuDevice = properties.getGpuDevice();
        return new WhisperNative(properties.getModelPath(), config);
    }

    /**
     * Provides a default WhisperTranscriptionModel bean if none is defined.
     * If a {@link WhisperNative} bean is available (from {@link #whisperNative}),
     * it is automatically wired into the model.
     *
     * @param waveService   the WaveService dependency
     * @param whisperNative an optional WhisperNative bean (maybe absent)
     * @return a new WhisperTranscriptionModel instance
     */
    @Bean
    @ConditionalOnMissingBean
    public WhisperTranscriptionModel whisperService(WaveService waveService, Optional<WhisperNative> whisperNative) {
        WhisperTranscriptionModel model = new WhisperTranscriptionModel(waveService);
        whisperNative.ifPresent(model::initWhisperNative);
        return model;
    }

}
