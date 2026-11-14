package io.github.ferdinandmehlan.whisperspring.audio;

/**
 * Thrown when an audio resource cannot be turned into the samples expected by Whisper.
 * Carries a {@link Reason} so that HTTP layers can map the failure to a meaningful status code.
 */
public class AudioDecodingException extends RuntimeException {

    /**
     * Classifies why audio decoding failed.
     */
    public enum Reason {

        /**
         * The audio container or codec is not supported by the active decoder.
         */
        UNSUPPORTED_FORMAT,

        /**
         * No decoder is installed or configured, e.g. ffmpeg is not on the PATH.
         */
        DECODER_UNAVAILABLE,

        /**
         * The audio is longer than the configured maximum duration.
         */
        LIMIT_EXCEEDED,

        /**
         * The decoder did not finish within the configured timeout.
         */
        TIMEOUT,

        /**
         * The audio could not be read, or the decoder failed unexpectedly.
         */
        FAILED
    }

    private final Reason reason;

    /**
     * Creates a new exception.
     *
     * @param reason  the failure reason
     * @param message a message that is safe to show to a client
     */
    public AudioDecodingException(Reason reason, String message) {
        this(reason, message, null);
    }

    /**
     * Creates a new exception.
     *
     * @param reason  the failure reason
     * @param message a message that is safe to show to a client
     * @param cause   the underlying cause, may be {@code null}
     */
    public AudioDecodingException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    /**
     * Returns the failure reason.
     *
     * @return the failure reason
     */
    public Reason getReason() {
        return reason;
    }
}
