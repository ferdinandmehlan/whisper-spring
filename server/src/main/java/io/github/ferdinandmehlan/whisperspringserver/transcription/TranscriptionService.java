package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionEvent;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Service for handling audio transcription requests.
 *
 * <p>The Whisper context is not safe for concurrent use, so every transcription is serialized
 * through a single lock. Decoding uploaded audio is delegated to the {@link WaveService} and
 * happens outside that lock wherever possible, so that a slow decoder does not block other
 * transcriptions.
 */
@Service
public class TranscriptionService {

    private static final Logger log = LoggerFactory.getLogger(TranscriptionService.class);

    private final WhisperTranscriptionModel model;
    private final WaveService waveService;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a new TranscriptionService with required dependencies.
     *
     * @param whisperTranscriptionModel the underlying whisper service
     * @param waveService               the service that decodes uploaded audio to samples
     */
    public TranscriptionService(WhisperTranscriptionModel whisperTranscriptionModel, WaveService waveService) {
        this.model = whisperTranscriptionModel;
        this.waveService = waveService;
    }

    /**
     * Performs audio transcription using the configured Whisper model.
     * Decodes the upload and transcribes it while holding the Whisper lock.
     *
     * @param audioFile the audio file resource to transcribe
     * @param config    the whisper transcription configuration
     * @return the transcription with timestamps and text metadata
     */
    public WhisperTranscription transcribe(Resource audioFile, WhisperTranscriptionOptions config) {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile, config);
        return withLock(() -> model.call(prompt).getResult());
    }

    /**
     * Streams the segments of a transcription as server sent events.
     *
     * @param audioFile the audio file resource to transcribe
     * @param config    the whisper transcription configuration, completed for streaming
     * @return a flux of server sent events, one per transcribed segment
     */
    public Flux<ServerSentEvent<TranscriptionEvent>> stream(Resource audioFile, WhisperTranscriptionOptions config) {
        float[] samples = waveService.toWaveSamples(audioFile);

        Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink =
                Sinks.many().unicast().onBackpressureBuffer();
        NewSegmentCallback callback = new NewSegmentCallback(model.getWhisperNative(), sink);
        config.newSegmentCallback = callback;
        config.tokenTimestamps = true;

        Thread.ofVirtual().name("transcription-stream").start(() -> {
            try {
                transcribeSamples(samples, config);
                if (callback.failure() != null) {
                    sink.tryEmitError(callback.failure());
                } else {
                    sink.tryEmitComplete();
                }
            } catch (Exception e) {
                sink.tryEmitError(e);
            }
        });

        return sink.asFlux().onTerminateDetach();
    }

    /**
     * Performs audio transcription on already decoded samples.
     *
     * @param samples the audio samples to transcribe, mono and 16kHz
     * @param config  the whisper transcription configuration
     * @return the transcription with timestamps and text metadata
     */
    public WhisperTranscription transcribeSamples(float[] samples, WhisperTranscriptionOptions config) {
        return withLock(() -> model.call(samples, config));
    }

    /**
     * Runs the given work while holding the Whisper lock. The native context is not safe for
     * concurrent use, so every transcription has to be serialized.
     *
     * @param body the transcription work
     * @param <T>  the result type
     * @return the result of the work
     */
    private <T> T withLock(Supplier<T> body) {
        lock.lock();
        log.info("Entering lock for transcription service");
        try {
            return body.get();
        } finally {
            log.info("Exiting lock for transcription service");
            lock.unlock();
        }
    }
}
