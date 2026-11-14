package io.github.ferdinandmehlan.whisperspringtestcommon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Enables a test only when an {@code ffmpeg} executable is available on the PATH.
 *
 * <p>Tests annotated with this are skipped, with the reason "ffmpeg is not available", on machines
 * without ffmpeg. A warning is logged once per test JVM. Availability is detected by running a
 * plain {@code ffmpeg -version} probe, so installing ffmpeg is enough to enable the tests - no
 * property or flag is involved.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(FfmpegAvailableCondition.class)
public @interface EnabledIfFfmpeg {}
