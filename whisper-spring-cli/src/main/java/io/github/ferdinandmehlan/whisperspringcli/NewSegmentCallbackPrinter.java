package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTokenDetail;
import io.github.ferdinandmehlan.whisperspring._native.callback.DefaultWhisperNewSegmentCallback;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * New segment callback implementation for whisper transcription.
 * Prints new segments in real-time as they become available.
 */
public class NewSegmentCallbackPrinter extends DefaultWhisperNewSegmentCallback {

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

    private static final Logger logger = LoggerFactory.getLogger(NewSegmentCallbackPrinter.class);

    public NewSegmentCallbackPrinter(
            WhisperNative whisper, boolean noTimestamps, boolean printColors, boolean printSpecial, PrintStream err) {
        super(whisper);
        this.noTimestamps = noTimestamps;
        this.printColors = printColors;
        this.printSpecial = printSpecial;
        this.err = err;
    }

    @Override
    public void handle(WhisperSegment segment) {
        if (segment.start() == 0) {
            err.println();
        }

        if (!noTimestamps) {
            String timestamp =
                    String.format("[%s --> %s]  ", formatTimestamp(segment.start()), formatTimestamp(segment.end()));
            err.print(timestamp);
        }

        if (printColors) {
            StringBuilder segmentOutput = new StringBuilder();
            for (WhisperTokenDetail tokenDetail : segment.tokens()) {
                double colorIndex = Math.pow(tokenDetail.probability(), 3) * COLORS.length;
                int col = Math.max(0, Math.min(COLORS.length - 1, (int) colorIndex));
                segmentOutput.append(COLORS[col]).append(tokenDetail.token()).append(RESET);
            }
            err.println(segmentOutput);
        } else {
            err.println(segment.text());
        }
    }

    @Override
    public void handleError(Throwable t) {
        logger.error("Failed to execute segment printer: ", t);
    }

    private String formatTimestamp(long time) {
        long totalSeconds = time / 100;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long millis = time % 100;

        return String.format("%02d:%02d:%02d.%02d0", hours, minutes, seconds, millis);
    }
}
