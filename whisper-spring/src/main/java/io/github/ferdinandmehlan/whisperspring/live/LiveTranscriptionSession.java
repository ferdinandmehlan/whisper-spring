package io.github.ferdinandmehlan.whisperspring.live;

import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import java.io.InterruptedIOException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a single live transcription session.
 * Receives raw PCM audio data, buffers it in an {@link AudioRingBuffer},
 * and processes it in a dedicated virtual thread. Results are delivered
 * to the provided {@link Consumer} callback.
 */
public class LiveTranscriptionSession implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(LiveTranscriptionSession.class);

    private static final int CHANNELS = 1;
    private static final int SAMPLE_RATE = 16000;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int CHUNK_SECONDS = 5;
    private static final int CHUNK_BYTES = CHUNK_SECONDS * SAMPLE_RATE * BYTES_PER_SAMPLE;
    private static final int BUFFER_SIZE = CHUNK_BYTES * 10;

    private final String sessionId;
    private final WhisperTranscriptionModel model;
    private final WaveService waveService;
    private final WhisperTranscriptionOptions options;
    private final Consumer<WhisperTranscription> resultListener;
    private final Thread consumeThread;
    private final AudioRingBuffer ringBuffer =
            new AudioRingBuffer(BUFFER_SIZE, SAMPLE_RATE, CHANNELS, BYTES_PER_SAMPLE);

    private volatile boolean closed;

    /**
     * Creates a new live transcription session and immediately starts
     * a virtual consumer thread that transcribes buffered audio chunks.
     *
     * @param sessionId      the unique session identifier
     * @param model          the Whisper transcription model
     * @param waveService    the wave service for PCM-to-float conversion
     * @param options        the transcription options
     * @param resultListener callback invoked with each transcription result
     */
    public LiveTranscriptionSession(
            String sessionId,
            WhisperTranscriptionModel model,
            WaveService waveService,
            WhisperTranscriptionOptions options,
            Consumer<WhisperTranscription> resultListener) {
        this.sessionId = sessionId;
        this.model = model;
        this.waveService = waveService;
        this.options = options;
        this.resultListener = resultListener;
        this.consumeThread = Thread.ofVirtual().name("consume-" + sessionId).start(this::consumeAudio);
    }

    /**
     * Appends raw PCM audio data to the ring buffer for transcription.
     * No-op after the session has been closed.
     *
     * @param pcmData the raw PCM 16-bit little-endian audio data
     */
    public void appendAudio(byte[] pcmData) {
        if (!closed) {
            ringBuffer.enqueue(pcmData);
        }
    }

    private void consumeAudio() {
        log.debug("session {}: consumer loop started", sessionId);
        try {
            while (!closed) {
                byte[] window = ringBuffer.dequeue(CHUNK_BYTES);
                float[] samples = waveService.bytesToWaveSamples(window);
                WhisperTranscription transcription = model.call(samples, options);
                resultListener.accept(transcription);
            }
        } catch (InterruptedIOException e) {
            log.info("session {}: transcription loop safely interrupted for shutdown", sessionId);
        } catch (Exception e) {
            log.error("session {}: consumer loop failed", sessionId, e);
        }
        log.debug("session {}: consumer loop ended", sessionId);
    }

    /**
     * Closes the session, signalling the consumer thread to shut down.
     */
    @Override
    public void close() {
        if (closed) return;
        closed = true;
        log.debug("session {}: closing session", sessionId);
        if (consumeThread != null) {
            consumeThread.interrupt();
        }
    }
}
