package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperProgressCallback;
import java.io.PrintStream;
import java.lang.foreign.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Progress callback implementation for whisper transcription.
 * Prints progress updates at regular intervals during processing.
 */
public class WhisperProgressPrinter implements WhisperProgressCallback {

    private final PrintStream out;
    private static final int progressStep = 5;
    private int progressPrev = 0;

    private static final Logger logger = LoggerFactory.getLogger(WhisperProgressPrinter.class);

    public WhisperProgressPrinter(PrintStream err) {
        this.out = err;
    }

    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData) {
        progress = Math.min(progress, 100);
        if (progress >= progressPrev + progressStep) {
            progressPrev += progressStep;
            out.println("[ Progress: " + progress + "% ]");
        }
    }
}
