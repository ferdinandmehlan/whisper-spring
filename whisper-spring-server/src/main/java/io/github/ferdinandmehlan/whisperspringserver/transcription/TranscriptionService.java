package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring.WhisperService;
import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperContextConfig;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import io.github.ferdinandmehlan.whisperspringserver.WhisperServerConfiguration;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Service for handling audio transcription using Whisper.
 * Manages Whisper context initialization and provides thread-safe transcription operations.
 */
@Service
public class TranscriptionService {

    private final WhisperServerConfiguration config;
    private final WhisperService whisperService;
    private WhisperNative whisper;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a new TranscriptionService with required dependencies.
     *
     * @param config the server configuration
     * @param whisperService the underlying whisper service
     */
    @Autowired
    public TranscriptionService(WhisperServerConfiguration config, WhisperService whisperService) {
        this.config = config;
        this.whisperService = whisperService;
    }

    /**
     * Initializes the Whisper context after bean construction.
     * Configures GPU usage and flash attention based on server configuration.
     *
     * @throws IOException if the model file cannot be found
     */
    @PostConstruct
    public void init() throws IOException {
        WhisperContextConfig contextConfig = new WhisperContextConfig();
        contextConfig.useGpu = !config.isNoGpu();
        contextConfig.flashAttn = config.isFlashAttn();
        whisper = new WhisperNative(config.getModel(), contextConfig);
    }

    /**
     * Performs audio transcription using the configured Whisper model.
     * This method is thread-safe and uses locking to ensure sequential access to the Whisper context.
     *
     * @param config the whisper transcription configuration
     * @param audioFile the audio file resource to transcribe
     * @return list of transcription segments with timestamps and text
     */
    public List<WhisperSegment> transcribe(WhisperTranscribeConfig config, Resource audioFile) {
        lock.lock();
        try {
            return whisperService.transcribe(whisper, config, audioFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
