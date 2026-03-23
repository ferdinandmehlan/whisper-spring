package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring._native.callback.DefaultWhisperProgressCallback;
import java.io.PrintStream;

/**
 * Progress callback implementation for whisper transcription.
 * Prints progress updates at regular intervals during processing.
 */
public class ProgressCallbackPrinter extends DefaultWhisperProgressCallback {

    private final PrintStream out;
    private static final int progressStep = 5;
    private int progressPrev = 0;

    public ProgressCallbackPrinter(PrintStream err) {
        this.out = err;
    }

    @Override
    public void handle(int progress) {
        if (progress >= progressPrev + progressStep) {
            progressPrev += progressStep;
            out.println("[ Progress: " + progress + "% ]");
        }
    }
}
