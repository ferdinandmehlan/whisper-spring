package io.github.ferdinandmehlan.whisperspring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Autoconfiguration class for Whisper Spring integration.
 * Provides default beans for WaveService and WhisperService
 * if they are not already defined in the application context.
 */
@Configuration
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
     * Provides a default WhisperService bean if none is defined.
     *
     * @param waveService the WaveService dependency
     * @return a new WhisperService instance with default dependencies
     */
    @Bean
    @ConditionalOnMissingBean
    public WhisperService whisperService(WaveService waveService) {
        return new WhisperService(waveService);
    }
}
