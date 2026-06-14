package io.github.ferdinandmehlan.whisperspring.live;

import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscription;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import java.io.InterruptedIOException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveTranscriptionSession implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(LiveTranscriptionSession.class);

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
    private final AudioRingBuffer ringBuffer = new AudioRingBuffer(BUFFER_SIZE);

    private volatile boolean closed;

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
        this.consumeThread = Thread.ofVirtual()
                .name("consume-" + sessionId)
                .start(this::consumeAudio);
    }

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

                var transcription = model.getWhisperNative().transcribe(samples, options);
                resultListener.accept(transcription);
            }
        } catch (InterruptedIOException e) {
            log.info("session {}: transcription loop safely interrupted for shutdown", sessionId);
        } catch (Exception e) {
            log.error("session {}: consumer loop failed", sessionId, e);
        }
        log.debug("session {}: consumer loop ended", sessionId);
    }

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