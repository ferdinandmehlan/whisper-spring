package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionResponse;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;

/**
 * Component for transcribing audio files using the Whisper speech recognition engine.
 * Handles audio transcription with configurable parameters. Audio decoding is delegated to the
 * {@link WaveService}, transcription to the {@link WhisperNative} context; both are immutable
 * collaborators, so an instance is fully initialized once constructed.
 * Managed by {@link WhisperAutoConfiguration}, which creates the bean only when a model is
 * configured.
 */
public class WhisperTranscriptionModel implements TranscriptionModel {

    private static final Logger log = LoggerFactory.getLogger(WhisperTranscriptionModel.class);

    private final WaveService waveService;
    private final WhisperNative whisperNative;

    /**
     * Creates a new WhisperTranscriptionModel.
     *
     * @param waveService the wave service for audio processing
     * @param whisperNative the loaded whisper context used for transcription
     */
    public WhisperTranscriptionModel(WaveService waveService, WhisperNative whisperNative) {
        this.waveService = Objects.requireNonNull(waveService, "waveService");
        this.whisperNative = Objects.requireNonNull(whisperNative, "whisperNative");
    }

    /**
     * Returns the whisper context this model transcribes with.
     * Callbacks that need to read segments from the native context, such as streaming
     * transcriptions, are constructed with it.
     *
     * @return the whisper context, never {@code null}
     */
    public WhisperNative getWhisperNative() {
        return whisperNative;
    }

    /**
     * Transcribes an audio file using default configuration.
     *
     * @param prompt the prompt containing the resource and options
     * @return audio transcription response
     */
    @Override
    public WhisperTranscriptionResponse call(AudioTranscriptionPrompt prompt) {
        WhisperTranscriptionOptions options = switch (prompt.getOptions()) {
            case null -> new WhisperTranscriptionOptions();
            case WhisperTranscriptionOptions opts -> opts;
            default ->
                throw new IllegalArgumentException(
                        "Unsupported options type [%s]. Whisper Native provider requires an instance of WhisperTranscriptionOptions."
                                .formatted(prompt.getOptions().getClass().getName()));
        };

        float[] samples = waveService.toWaveSamples(prompt.getInstructions());
        return new WhisperTranscriptionResponse(call(samples, options));
    }

    /**
     * Transcribes already decoded samples, mono and 16kHz.
     *
     * @param samples the samples to transcribe
     * @param options the transcription options
     * @return the transcription result
     */
    public WhisperTranscription call(float[] samples, WhisperTranscriptionOptions options) {
        log.debug("Transcribing samples");
        WhisperTranscription transcription = whisperNative.transcribe(samples, options);
        log.debug("Finished transcribing samples");
        return transcription;
    }
}
