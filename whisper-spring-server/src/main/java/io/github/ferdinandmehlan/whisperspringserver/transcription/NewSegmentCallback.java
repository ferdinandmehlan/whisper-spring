package io.github.ferdinandmehlan.whisperspringserver.transcription;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperNewSegmentCallback;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TokenDetail;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionEvent;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

public class NewSegmentCallback implements WhisperNewSegmentCallback {

    private final WhisperNative whisper;
    private final Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink;

    public NewSegmentCallback(WhisperNative whisper, Sinks.Many<ServerSentEvent<TranscriptionEvent>> sink) {
        this.whisper = whisper;
        this.sink = sink;
    }

    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
        try {
            int nSegments = whisper.fullNSegments(ctx);
            int s0 = nSegments - nNew;
            int eotToken = whisper.tokenEot(ctx);

            for (int i = s0; i < nSegments; i++) {
                long t0 = whisper.fullGetSegmentT0(ctx, i);
                long t1 = whisper.fullGetSegmentT1(ctx, i);
                MemorySegment textSegment = whisper.fullGetSegmentText(ctx, i);
                String text = textSegment.reinterpret(1000).getString(0);

                int nTokens = whisper.fullNTokens(ctx, i);
                List<TokenDetail> tokens = new ArrayList<>(nTokens);
                for (int j = 0; j < nTokens; j++) {
                    int tokenId = whisper.fullGetTokenId(ctx, i, j);
                    if (tokenId >= eotToken) {
                        continue;
                    }
                    MemorySegment tokenTextSegment = whisper.fullGetTokenText(ctx, i, j);
                    String tokenText = tokenTextSegment.reinterpret(1000).getString(0);
                    float probability = whisper.fullGetTokenP(ctx, i, j);
                    tokens.add(new TokenDetail(tokenText, probability));
                }

                TranscriptionEvent event = new TranscriptionEvent(t0, t1, text.trim(), tokens);
                ServerSentEvent<TranscriptionEvent> sseEvent = ServerSentEvent.<TranscriptionEvent>builder()
                        .event("segment")
                        .data(event)
                        .build();
                sink.emitNext(sseEvent, Sinks.EmitFailureHandler.FAIL_FAST);
            }
        } catch (Throwable t) {
            sink.emitError(t, Sinks.EmitFailureHandler.FAIL_FAST);
        }
    }
}
