package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring.loader.NativeExtractor;
import io.github.ferdinandmehlan.whisperspring.loader.WhisperNativeLoader;
import io.github.ferdinandmehlan.whisperspring.loader.WhisperProperties;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Autoconfiguration class for Whisper Spring integration.
 * Provides default beans for WaveService, NativeExtractor, WhisperParamsMapper, WhisperNativeLoader, and WhisperService
 * if they are not already defined in the application context.
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
     * Provides a default NativeExtractor bean if none is defined.
     *
     * @return a new NativeExtractor instance
     * @throws IOException if temporary directory creation fails
     */
    @Bean
    @ConditionalOnMissingBean
    public NativeExtractor nativeExtractor() throws IOException {
        return new NativeExtractor();
    }

    /**
     * Provides a default WhisperNativeLoader bean if none is defined.
     *
     * @return a new WhisperNativeLoader instance
     */
    @Bean
    @ConditionalOnMissingBean
    public WhisperNativeLoader whisperNativeLoader() {
        return new WhisperNativeLoader();
    }

    /**
     * Provides a default WhisperParamsMapper bean if none is defined.
     *
     * @return a new WhisperParamsMapper instance
     */
    @Bean
    @ConditionalOnMissingBean
    public WhisperParamsMapper whisperParamsMapper() {
        return new WhisperParamsMapper();
    }

    /**
     * Provides a default WhisperService bean if none is defined.
     *
     * @return a new WhisperService instance with default dependencies
     */
    @Bean
    @ConditionalOnMissingBean
    @DependsOn("whisperNativeLoader")
    public WhisperService whisperService() {
        return new WhisperService(whisperParamsMapper(), waveService());
    }
}
