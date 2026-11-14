package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.callback.DefaultWhisperNewSegmentCallback;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionEvent;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

/**
 * Callback handler for streaming new transcription segments to clients via Server-Sent Events.
 * Converts Whisper segments into SSE events for real-time transcription updates.
 */
public class NewSegmentCallback extends DefaultWhisperNewSegmentCallback {

    private static final Logger log = LoggerFactory.getLogger(NewSegmentCallback.class);

    private final Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink;
    private final AtomicReference<Throwable> failure = new AtomicReference<>();

    /**
     * Creates a new callback for streaming segments to SSE clients.
     *
     * @param whisper the Whisper native instance
     * @param sink the SSE sink for emitting events
     */
    public NewSegmentCallback(WhisperNative whisper, Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink) {
        super(whisper);
        this.sink = sink;
    }

    @Override
    public void handle(WhisperSegment segment) {
        log.debug("New segment from callback received: {}", segment);

        ServerSentEvent<TranscriptionEvent> sseEvent = ServerSentEvent.<TranscriptionEvent>builder()
                .event("segment")
                .data(TranscriptionEvent.from(segment))
                .build();
        sink.emitNext(sseEvent, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Override
    public void handleError(Throwable t) {
        log.error("Failed to read a segment from the Whisper context", t);
        failure.set(t);
    }

    /**
     * Returns the error that occurred while reading segments, if any.
     *
     * @return the failure, or {@code null} if all segments were read successfully
     */
    public Throwable failure() {
        return failure.get();
    }
}
