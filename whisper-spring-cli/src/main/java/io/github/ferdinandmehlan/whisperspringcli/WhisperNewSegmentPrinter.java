package io.github.ferdinandmehlan.whisperspringcli;

import com.sun.jna.Pointer;
import io.github.ggerganov.whispercpp.WhisperCppJnaLibrary;
import io.github.ggerganov.whispercpp.callbacks.WhisperNewSegmentCallback;
import java.io.PrintStream;

/**
 * New segment callback implementation for whisper transcription.
 * Prints new segments in real-time as they become available.
 */
public class WhisperNewSegmentPrinter implements WhisperNewSegmentCallback {

    private final WhisperCppJnaLibrary lib = WhisperCppJnaLibrary.instance;
    private final boolean noTimestamps;
    private final boolean printColors;
    private final boolean printSpecial;
    private final PrintStream err;

    // ANSI color codes (similar to k_colors in C++)
    private static final String[] COLORS = {
        "\033[38;5;196m", // Red (low confidence)
        "\033[38;5;208m", // Orange
        "\033[38;5;214m", // Yellow-orange
        "\033[38;5;220m", // Yellow
        "\033[38;5;190m", // Yellow-green
        "\033[38;5;118m", // Green
        "\033[38;5;46m" // Bright green (high confidence)
    };
    private static final String RESET = "\033[0m";

    /**
     * Constructs a new segment printer with specified options.
     *
     * @param noTimestamps whether to omit timestamps in output
     * @param printColors whether to color-code tokens by confidence
     * @param printSpecial whether to include special tokens
     * @param err the stream to print segments to
     */
    public WhisperNewSegmentPrinter(boolean noTimestamps, boolean printColors, boolean printSpecial, PrintStream err) {
        this.noTimestamps = noTimestamps;
        this.printColors = printColors;
        this.printSpecial = printSpecial;
        this.err = err;
    }

    /**
     * Callback invoked when new segments are available during transcription.
     * Prints the new segments to the configured output stream.
     *
     * @param ctx the Whisper context pointer
     * @param state the Whisper state pointer
     * @param n_new number of new segments available
     * @param user_data user data pointer (unused)
     */
    @Override
    public void callback(Pointer ctx, Pointer state, int n_new, Pointer user_data) {
        // Get the number of segments
        int n_segments = lib.whisper_full_n_segments(ctx);

        // Print the last n_new segments
        int s0 = n_segments - n_new;

        if (s0 == 0) {
            err.println();
        }

        for (int i = s0; i < n_segments; i++) {
            if (!noTimestamps) {
                long t0 = lib.whisper_full_get_segment_t0(ctx, i) * 10;
                long t1 = lib.whisper_full_get_segment_t1(ctx, i) * 10;
                String timestamp = String.format("[%s --> %s]  ", formatTimestamp(t0), formatTimestamp(t1));
                err.print(timestamp);
            }

            StringBuilder segmentOutput = new StringBuilder();
            if (printColors) {
                // Print with colors - iterate through tokens
                int n_tokens = lib.whisper_full_n_tokens(ctx, i);
                for (int j = 0; j < n_tokens; j++) {
                    // Skip special tokens unless print_special is true
                    if (!printSpecial) {
                        int token_id = lib.whisper_full_get_token_id(ctx, i, j);
                        int eot_token = lib.whisper_token_eot(ctx);
                        if (token_id >= eot_token) {
                            continue;
                        }
                    }

                    String token_text = lib.whisper_full_get_token_text(ctx, i, j);
                    float probability = lib.whisper_full_get_token_p(ctx, i, j);

                    double color_index = Math.pow(probability, 3) * COLORS.length;
                    int col = Math.max(0, Math.min(COLORS.length - 1, (int) color_index));

                    segmentOutput.append(COLORS[col]).append(token_text).append(RESET);
                }
            } else {
                // Print without colors - use full segment text
                String text = lib.whisper_full_get_segment_text(ctx, i);
                if (text != null) {
                    segmentOutput.append(text);
                }
            }
            err.println(segmentOutput);
        }

        // Flush output for real-time display
        err.flush();
    }

    /**
     * Formats milliseconds into HH:MM:SS.mmm timestamp string.
     *
     * @param milliseconds the time in milliseconds
     * @return formatted timestamp string
     */
    private String formatTimestamp(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long millis = milliseconds % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
