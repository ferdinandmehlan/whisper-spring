package io.github.ferdinandmehlan.whisperspringtestcommon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Evaluates whether an {@code ffmpeg} executable can be started.
 * Used by {@link EnabledIfFfmpeg}.
 *
 * <p>The probe runs once per JVM and is cached. When ffmpeg is missing, a single warning is logged
 * and the annotated tests are skipped with the reason "ffmpeg is not available".
 */
public class FfmpegAvailableCondition implements ExecutionCondition {

    private static final System.Logger log = System.getLogger(FfmpegAvailableCondition.class.getName());

    private static final String COMMAND = "ffmpeg";
    private static final Duration PROBE_TIMEOUT = Duration.ofSeconds(10);
    private static final String SKIP_REASON = "ffmpeg is not available";

    private static volatile Boolean available;

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (isAvailable()) {
            return ConditionEvaluationResult.enabled("ffmpeg is available");
        }
        return ConditionEvaluationResult.disabled(SKIP_REASON);
    }

    private static boolean isAvailable() {
        Boolean cached = available;
        if (cached != null) {
            return cached;
        }
        boolean found = probe();
        available = found;
        if (!found) {
            log.log(
                    System.Logger.Level.WARNING,
                    "ffmpeg is not available on the PATH - skipping tests annotated with @EnabledIfFfmpeg");
        }
        return found;
    }

    private static boolean probe() {
        Process process = null;
        AtomicBoolean timedOut = new AtomicBoolean(false);
        try {
            process = new ProcessBuilder(List.of(COMMAND, "-version"))
                    .redirectErrorStream(true)
                    .start();
            Process started = process;
            Thread watchdog = Thread.ofVirtual()
                    .name("ffmpeg-condition-watchdog")
                    .start(() -> {
                        try {
                            if (!started.waitFor(PROBE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                                timedOut.set(true);
                                started.destroyForcibly();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
            // Drain the complete output: closing the stream early makes ffmpeg fail with SIGPIPE
            // (exit code 141), which would look like a missing installation.
            try (InputStream in = process.getInputStream()) {
                in.transferTo(OutputStream.nullOutputStream());
            }
            watchdog.join(PROBE_TIMEOUT.toMillis());
            return process.waitFor(PROBE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                    && !timedOut.get()
                    && process.exitValue() == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }
}
