package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperNewSegmentCallback;
import java.io.PrintStream;
import java.lang.foreign.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * New segment callback implementation for whisper transcription.
 * Prints new segments in real-time as they become available.
 */
public class WhisperNewSegmentPrinter implements WhisperNewSegmentCallback {

    private final WhisperNative whisper;
    private final boolean noTimestamps;
    private final boolean printColors;
    private final boolean printSpecial;
    private final PrintStream err;

    private static final String[] COLORS = {
        "\033[38;5;196m",
        "\033[38;5;208m",
        "\033[38;5;214m",
        "\033[38;5;220m",
        "\033[38;5;190m",
        "\033[38;5;118m",
        "\033[38;5;46m"
    };
    private static final String RESET = "\033[0m";

    private static final Logger logger = LoggerFactory.getLogger(WhisperNewSegmentPrinter.class);

    public WhisperNewSegmentPrinter(
            WhisperNative whisper, boolean noTimestamps, boolean printColors, boolean printSpecial, PrintStream err) {
        this.whisper = whisper;
        this.noTimestamps = noTimestamps;
        this.printColors = printColors;
        this.printSpecial = printSpecial;
        this.err = err;
    }

    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
        try {
            int nSegments = whisper.fullNSegments(ctx);
            int s0 = nSegments - nNew;

            if (s0 == 0) {
                err.println();
            }

            for (int i = s0; i < nSegments; i++) {
                if (!noTimestamps) {
                    long t0 = whisper.fullGetSegmentT0(ctx, i) * 10;
                    long t1 = whisper.fullGetSegmentT1(ctx, i) * 10;
                    String timestamp = String.format("[%s --> %s]  ", formatTimestamp(t0), formatTimestamp(t1));
                    err.print(timestamp);
                }

                StringBuilder segmentOutput = new StringBuilder();
                if (printColors) {
                    int nTokens = whisper.fullNTokens(ctx, i);
                    for (int j = 0; j < nTokens; j++) {
                        if (!printSpecial) {
                            int tokenId = whisper.fullGetTokenId(ctx, i, j);
                            int eotToken = whisper.tokenEot(ctx);
                            if (tokenId >= eotToken) {
                                continue;
                            }
                        }

                        MemorySegment tokenTextSegment = whisper.fullGetTokenText(ctx, i, j);
                        String tokenText = tokenTextSegment.reinterpret(1000).getString(0);
                        float probability = whisper.fullGetTokenP(ctx, i, j);
                        double colorIndex = Math.pow(probability, 3) * COLORS.length;
                        int col = Math.max(0, Math.min(COLORS.length - 1, (int) colorIndex));

                        segmentOutput.append(COLORS[col]).append(tokenText).append(RESET);
                    }
                } else {
                    MemorySegment textSegment = whisper.fullGetSegmentText(ctx, i);
                    String text = textSegment.reinterpret(1000).getString(0);
                    if (text != null) {
                        segmentOutput.append(text);
                    }
                }
                err.println(segmentOutput);
            }

            err.flush();
        } catch (Throwable t) {
            logger.error("Failed to execute segment printer: ", t);
        }
    }

    private String formatTimestamp(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long millis = milliseconds % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
