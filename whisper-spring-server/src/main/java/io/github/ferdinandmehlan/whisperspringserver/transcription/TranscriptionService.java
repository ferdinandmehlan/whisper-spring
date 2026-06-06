package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionResponse;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionEvent;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
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

    private final WhisperTranscriptionModel model;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a new TranscriptionService with required dependencies.
     *
     * @param whisperTranscriptionModel the underlying whisper service
     */
    public TranscriptionService(WhisperTranscriptionModel whisperTranscriptionModel) {
        this.model = whisperTranscriptionModel;
    }

    /**
     * Performs audio transcription using the configured Whisper model.
     * This method is thread-safe and uses locking to ensure sequential access to the Whisper context.
     *
     * @param audioFile the audio file resource to transcribe
     * @param config    the whisper transcription configuration
     * @return WhisperTranscriptionResponse with timestamps and text metadata
     */
    public WhisperTranscription transcribe(Resource audioFile, WhisperTranscriptionOptions config) {
        lock.lock();
        log.info("Entering lock for transcription service");

        try {
            AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile, config);
            WhisperTranscriptionResponse transcriptionResponse = model.call(prompt);
            return transcriptionResponse.getResult();
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
    public Sinks.Many<ServerSentEvent<TranscriptionEvent>> createSSESink(WhisperTranscriptionOptions config) {
        Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink =
                Sinks.many().unicast().onBackpressureBuffer();
        config.newSegmentCallback = new NewSegmentCallback(model.getWhisperNative(), sink);
        config.tokenTimestamps = true;
        return sink;
    }
}
