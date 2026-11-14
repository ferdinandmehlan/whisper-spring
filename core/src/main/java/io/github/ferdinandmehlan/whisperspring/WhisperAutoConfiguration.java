package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.loader.NativeLibrary;
import io.github.ferdinandmehlan.whisperspring.audio.AudioDecoder;
import io.github.ferdinandmehlan.whisperspring.audio.FfmpegAudioDecoder;
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
 * When {@code whisper.model-path} and {@code whisper.native-library-path} are configured, a
 * {@link WhisperNative} bean is created and a transcription model is wired with it; without a
 * model there is no transcription model bean at all, instead of one that fails on first use.
 * An {@link AudioDecoder} bean backed by ffmpeg is provided and used as fallback for audio the JDK
 * cannot read; declare your own {@link AudioDecoder} bean to replace it.
 */
@Configuration
@EnableConfigurationProperties(WhisperProperties.class)
public class WhisperAutoConfiguration {

    /**
     * Provides an ffmpeg backed audio decoder if none is defined. The decoder only starts a process
     * once audio actually needs to be decoded, so creating the bean never spawns anything by itself.
     *
     * @return a new {@link FfmpegAudioDecoder} instance
     */
    @Bean
    @ConditionalOnMissingBean
    public AudioDecoder audioDecoder() {
        return new FfmpegAudioDecoder();
    }

    /**
     * Provides a default WaveService bean if none is defined.
     *
     * @param audioDecoder an optional audio decoder bean
     * @return a new WaveService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public WaveService waveService(Optional<AudioDecoder> audioDecoder) {
        return new WaveService(audioDecoder.orElse(null));
    }

    /**
     * Loads the whisper native library from {@code whisper.native-library-path}.
     *
     * @param properties the whisper configuration properties
     * @return the loaded native library
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "whisper", name = "native-library-path")
    @ConditionalOnMissingBean
    public NativeLibrary whisperNativeLibrary(WhisperProperties properties) {
        return NativeLibrary.load(properties.getNativeLibraryPath());
    }

    /**
     * Creates a {@link WhisperNative} bean when {@code whisper.model-path} and
     * {@code whisper.native-library-path} are set.
     *
     * @param properties the whisper configuration properties
     * @param nativeLibrary the loaded native library
     * @return a new WhisperNative instance
     * @throws IOException if the model cannot be loaded
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "whisper",
            name = {"model-path", "native-library-path"})
    @ConditionalOnMissingBean
    public WhisperNative whisperNative(WhisperProperties properties, NativeLibrary nativeLibrary) throws IOException {
        return new WhisperNative(properties.getModelPath(), nativeLibrary);
    }

    /**
     * Provides a default WhisperTranscriptionModel bean if none is defined. The bean is only
     * created when a model is configured, because the model cannot transcribe without a
     * {@link WhisperNative} context.
     *
     * @param waveService   the WaveService dependency
     * @param whisperNative the loaded whisper context
     * @return a new WhisperTranscriptionModel instance
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "whisper",
            name = {"model-path", "native-library-path"})
    @ConditionalOnMissingBean
    public WhisperTranscriptionModel whisperTranscriptionModel(WaveService waveService, WhisperNative whisperNative) {
        return new WhisperTranscriptionModel(waveService, whisperNative);
    }
}
