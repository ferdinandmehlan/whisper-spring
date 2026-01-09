package io.github.ferdinandmehlan.whisperspringserver.inference;

import io.github.ferdinandmehlan.whisperspring.WhisperParams;
import io.github.ferdinandmehlan.whisperspring.WhisperService;
import io.github.ferdinandmehlan.whisperspringserver.WhisperServerConfiguration;
import io.github.ggerganov.whispercpp.WhisperCpp;
import io.github.ggerganov.whispercpp.bean.WhisperSegment;
import io.github.ggerganov.whispercpp.params.WhisperContextParams;
import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Service for handling audio transcription inference using Whisper.
 * Manages Whisper context initialization and provides thread-safe transcription operations.
 */
@Service
public class InferenceService {

    private final WhisperServerConfiguration config;
    private final WhisperService whisperService;
    private WhisperCpp whisper;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a new InferenceService with required dependencies.
     *
     * @param config the server configuration
     * @param whisperService the underlying whisper service
     */
    @Autowired
    public InferenceService(WhisperServerConfiguration config, WhisperService whisperService) {
        this.config = config;
        this.whisperService = whisperService;
    }

    /**
     * Initializes the Whisper context after bean construction.
     * Configures GPU usage and flash attention based on server configuration.
     *
     * @throws FileNotFoundException if the model file cannot be found
     */
    @PostConstruct
    public void init() throws FileNotFoundException {
        whisper = new WhisperCpp();
        WhisperContextParams.ByValue contextParams = whisper.getContextDefaultParams();
        contextParams.useGpu(!config.isNoGpu());
        contextParams.useFlashAttn(config.isFlashAttn());
        whisper.initContext(config.getModel(), contextParams);
    }

    /**
     * Performs audio transcription using the configured Whisper model.
     * This method is thread-safe and uses locking to ensure sequential access to the Whisper context.
     *
     * @param params the whisper transcription parameters
     * @param audioFile the audio file resource to transcribe
     * @return list of transcription segments with timestamps and text
     */
    public List<WhisperSegment> transcribe(WhisperParams params, Resource audioFile) {
        lock.lock();
        try {
            return whisperService.transcribe(whisper, params, audioFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
