package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring.WhisperService;
import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperContextConfig;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import io.github.ferdinandmehlan.whisperspringserver.WhisperServerConfiguration;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionEvent;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

/**
 * Service for handling audio transcription using Whisper.
 * Manages Whisper context initialization and provides thread-safe transcription operations.
 */
@Service
public class TranscriptionService {

    private static final Logger log = LoggerFactory.getLogger(TranscriptionService.class.getName());

    private final WhisperServerConfiguration config;
    private final WhisperService whisperService;
    private final ReentrantLock lock = new ReentrantLock();
    private WhisperNative whisper;

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
        log.info("Initializing transcription service");

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
        log.info("Entering lock for transcription service");

        try {
            return whisperService.transcribe(whisper, config, audioFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Exiting lock for transcription service");
            lock.unlock();
        }
    }

    /**
     *
     * @param config the whisper transcription configuration
     * @return Sink for Server Side Events created by whisper callbacks
     */
    public Sinks.Many<ServerSentEvent<TranscriptionEvent>> createSSESink(WhisperTranscribeConfig config) {
        Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink =
                Sinks.many().unicast().onBackpressureBuffer();
        config.newSegmentCallback = new NewSegmentCallback(whisper, sink);
        config.tokenTimestamps = true;
        return sink;
    }
}
