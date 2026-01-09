package io.github.ferdinandmehlan.whisperspringserver.inference;

/**
 * Enumeration of supported response formats for transcription results.
 */
public enum ResponseFormat {
    /** JSON format with detailed segments and metadata */
    JSON("json"),
    /** Plain text format with concatenated transcription */
    TEXT("text"),
    /** SRT subtitle format with timestamps */
    SRT("srt");

    private final String value;

    ResponseFormat(String value) {
        this.value = value;
    }

    /**
     * Converts a string value to the corresponding ResponseFormat enum.
     * Case-insensitive matching is performed.
     *
     * @param value the string representation of the format
     * @return the corresponding ResponseFormat enum value
     * @throws IllegalArgumentException if the value doesn't match any format
     */
    public static ResponseFormat fromString(String value) {
        for (ResponseFormat format : values()) {
            if (format.value.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unknown format: " + value);
    }
}
