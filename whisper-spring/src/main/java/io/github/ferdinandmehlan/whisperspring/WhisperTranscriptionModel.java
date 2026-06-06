package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.core.io.Resource;

/**
 * Component for transcribing audio files using the Whisper speech recognition engine.
 * Handles model loading and audio transcription with configurable parameters.
 * Managed by {@link WhisperAutoConfiguration}; when {@code whisper.model-path} is
 * configured the underlying {@link io.github.ferdinandmehlan.whisperspring._native.WhisperNative}
 * is wired automatically.
 */
public class WhisperTranscriptionModel implements TranscriptionModel {

    private static final Logger log = LoggerFactory.getLogger(WhisperTranscriptionModel.class.getName());

    private final WaveService waveService;

    private WhisperNative whisperNative;

    /**
     * Creates a new WhisperTranscriptionModel.
     *
     * @param waveService the wave service for audio processing
     */
    public WhisperTranscriptionModel(WaveService waveService) {
        this.waveService = waveService;
    }

    public void initWhisperNative(WhisperNative whisperNative) {
        this.whisperNative = whisperNative;
    }

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
        if (this.whisperNative == null) {
            throw new IllegalStateException(
                    "WhisperNative has not been initialized yet. Ensure initWhisperNative() is called before processing requests.");
        }

        WhisperTranscriptionOptions options =
                switch (prompt.getOptions()) {
                    case null -> new WhisperTranscriptionOptions();
                    case WhisperTranscriptionOptions opts -> opts;
                    default ->
                        throw new IllegalArgumentException(
                                "Unsupported options type [%s]. Whisper Native provider requires an instance of WhisperTranscriptionOptions."
                                        .formatted(
                                                prompt.getOptions().getClass().getName()));
                };

        Resource audioFile = prompt.getInstructions();
        log.info("Encoding wave samples from {}", audioFile.getFilename());
        float[] audioData = waveService.toWaveSamples(audioFile);
        log.info("Transcribing audio file: {}", audioFile.getFilename());
        WhisperTranscription transcription = whisperNative.transcribe(audioData, options);
        log.info("Finished transcribing audio file: {}", audioFile.getFilename());
        return new WhisperTranscriptionResponse(transcription);
    }
}
