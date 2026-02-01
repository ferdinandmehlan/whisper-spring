package io.github.ferdinandmehlan.whisperspring._native.loader;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspring.BaseIntegrationTest;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class NativeLoaderTest extends BaseIntegrationTest {

    @Test
    void tesLoadWhisperNative() throws Exception {
        Path whisperLibPath = NativeLoader.getWhisperLibPath();
        assertThat(whisperLibPath).isNotNull();
        assertThat(whisperLibPath.toString()).contains("whisper");
        assertThat(Files.exists(whisperLibPath)).isTrue();
    }
}
