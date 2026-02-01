package io.github.ferdinandmehlan.whisperspring._native.ffm;

/**
 * Sampling strategies available for Whisper transcription.
 * Corresponds to the whisper_sampling_strategy enum in whisper.h.
 */
public enum WhisperSamplingStrategy {
    /**
     * Greedy decoding - similar to OpenAI's GreedyDecoder.
     * Selects the most probable token at each step.
     */
    WHISPER_SAMPLING_GREEDY(0),

    /**
     * Beam search decoding - similar to OpenAI's BeamSearchDecoder.
     * Maintains multiple hypotheses and selects the best overall sequence.
     */
    WHISPER_SAMPLING_BEAM_SEARCH(1);

    private final int value;

    WhisperSamplingStrategy(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value matching the C enum values in whisper.h.
     *
     * @return the C enum integer value
     */
    public int getValue() {
        return value;
    }
}
