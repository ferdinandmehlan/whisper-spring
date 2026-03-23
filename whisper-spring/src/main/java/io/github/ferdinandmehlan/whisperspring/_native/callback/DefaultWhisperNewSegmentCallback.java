package io.github.ferdinandmehlan.whisperspring._native.callback;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTokenDetail;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract callback implementation that processes native Whisper segment data.
 * Converts low-level memory segment operations into high-level segment objects.
 */
public abstract class DefaultWhisperNewSegmentCallback implements WhisperNewSegmentCallback {

    private final WhisperNative whisper;

    /**
     * Creates a new callback handler with the given Whisper instance.
     *
     * @param whisper the Whisper native instance
     */
    public DefaultWhisperNewSegmentCallback(WhisperNative whisper) {
        this.whisper = whisper;
    }

    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
        try {
            // whisper_full_n_segments - Get the total number of token segments generated so far
            int nSegments = whisper.fullNSegments(ctx);
            // Calculate the starting index of new segments (segments - newly added segments)
            int s0 = nSegments - nNew;
            // whisper_token_eot - Get the End-Of-Text token id (special token marking end of transcription)
            int eotToken = whisper.tokenEot(ctx);

            for (int i = s0; i < nSegments; i++) {
                // whisper_full_get_segment_t0 - Get the start time of the segment in milliseconds
                long t0 = whisper.fullGetSegmentT0(ctx, i);
                // whisper_full_get_segment_t1 - Get the end time of the segment in milliseconds
                long t1 = whisper.fullGetSegmentT1(ctx, i);
                // whisper_full_get_segment_text - Get the transcribed token for this segment
                MemorySegment textSegment = whisper.fullGetSegmentText(ctx, i);
                String text = textSegment.reinterpret(1000).getString(0).trim();

                // whisper_full_n_tokens - Get the number of tokens in this segment
                int nTokens = whisper.fullNTokens(ctx, i);
                List<WhisperTokenDetail> tokens = new ArrayList<>(nTokens);
                for (int j = 0; j < nTokens; j++) {
                    // whisper_full_get_token_id - Get the token id
                    int tokenId = whisper.fullGetTokenId(ctx, i, j);
                    // Skip tokens that are EOT or beyond (they don't have meaningful token)
                    if (tokenId >= eotToken) {
                        continue;
                    }
                    // whisper_full_get_token_text - Get the token representation of the token
                    MemorySegment tokenSegment = whisper.fullGetTokenText(ctx, i, j);
                    String token = tokenSegment.reinterpret(1000).getString(0);
                    // whisper_full_get_token_p - Get the probability of this token (0.0 to 1.0)
                    float probability = whisper.fullGetTokenP(ctx, i, j);
                    tokens.add(new WhisperTokenDetail(token, probability));
                }

                // Call the abstract handle method with the processed segment data
                handle(new WhisperSegment(text, t0, t1, tokens));
            }

        } catch (Throwable t) {
            // Call the abstract handleError method with the throwable
            handleError(t);
        }
    }

    /**
     * Called for each new segment transcribed by Whisper.
     *
     * @param segment   The transcribed segment including the specific tokens and probabilities
     */
    public abstract void handle(WhisperSegment segment);

    /**
     * Called when an error occurs during segment processing.
     *
     * @param t The exception that was thrown
     */
    public abstract void handleError(Throwable t);
}
