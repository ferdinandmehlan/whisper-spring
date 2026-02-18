package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperNewSegmentCallback;
import java.lang.foreign.MemorySegment;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

public class NewSegmentCallback implements WhisperNewSegmentCallback {

    private final WhisperNative whisper;
    private final Sinks.Many<ServerSentEvent<WhisperSegment>> sink;

    public NewSegmentCallback(WhisperNative whisper, Sinks.Many<ServerSentEvent<WhisperSegment>> sink) {
        this.whisper = whisper;
        this.sink = sink;
    }

    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
        try {
            int nSegments = whisper.fullNSegments(ctx);
            int s0 = nSegments - nNew;

            for (int i = s0; i < nSegments; i++) {
                long t0 = whisper.fullGetSegmentT0(ctx, i);
                long t1 = whisper.fullGetSegmentT1(ctx, i);
                MemorySegment textSegment = whisper.fullGetSegmentText(ctx, i);
                String text = textSegment.reinterpret(1000).getString(0);

                WhisperSegment segment = new WhisperSegment(t0, t1, text.trim());
                ServerSentEvent event =
                        ServerSentEvent.builder().event("segment").data(segment).build();
                sink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
            }
        } catch (Throwable t) {
            sink.emitError(t, Sinks.EmitFailureHandler.FAIL_FAST);
        }
    }
}
