package io.github.ferdinandmehlan.whisperspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

public class WhisperTranscriptionModelTest extends BaseIntegrationTest {

    @Autowired
    private WhisperTranscriptionModel model;

    @Test
    public void testTranscribe() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        assertThat(audioFile.exists()).isTrue();

        String result = model.transcribe(audioFile);
        assertWithFile(result);
    }

    @Test
    public void testLoadModel() throws IOException {
        WhisperNative whisperNative = new WhisperNative("build/resources/test/ggml-tiny.bin");
        model.initWhisperNative(whisperNative);
        assertThat(whisperNative).isNotNull();
    }

    @Test
    public void testLoadModelNonExistentFile() {
        assertThatThrownBy(() -> new WhisperNative("src/test/resources/test-models/nonexistent.bin"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Failed to initialize WhisperNative");
    }
}
