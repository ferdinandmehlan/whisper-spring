package io.github.ferdinandmehlan.whisperspringcli;

import com.sun.jna.Pointer;
import io.github.ggerganov.whispercpp.callbacks.WhisperProgressCallback;
import java.io.PrintStream;

/**
 * Progress callback implementation for whisper transcription.
 * Prints progress updates at regular intervals during processing.
 */
public class WhisperProgressPrinter implements WhisperProgressCallback {

    private final PrintStream out;
    private static final int progressStep = 5;
    private int progressPrev = 0;

    /**
     * Constructs a progress printer with the specified output stream.
     *
     * @param err the stream to print progress messages to
     */
    public WhisperProgressPrinter(PrintStream err) {
        this.out = err;
    }

    /**
     * Callback invoked to report transcription progress.
     * Prints progress percentage at regular intervals.
     *
     * @param ctx the Whisper context pointer
     * @param state the Whisper state pointer
     * @param progress current progress percentage (0-100)
     * @param user_data user data pointer (unused)
     */
    @Override
    public void callback(Pointer ctx, Pointer state, int progress, Pointer user_data) {
        progress = Math.min(progress, 100);
        if (progress >= progressPrev + progressStep) {
            progressPrev += progressStep;
            out.println("[ Progress: " + progress + "% ]");
        }
    }
}
