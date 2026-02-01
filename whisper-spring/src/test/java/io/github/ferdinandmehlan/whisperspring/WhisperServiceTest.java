package io.github.ferdinandmehlan.whisperspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

public class WhisperServiceTest extends BaseIntegrationTest {

    @Autowired
    private WhisperService whisperService;

    @Test
    public void testTranscribe() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        assertThat(audioFile.exists()).isTrue();

        WhisperNative whisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        List<WhisperSegment> segments = whisperService.transcribe(whisper, audioFile);
        String result = segments.stream().map(WhisperSegment::text).collect(Collectors.joining("\n"));
        assertWithFile(result);
    }

    @Test
    public void testLoadModel() throws IOException {
        WhisperNative whisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        assertThat(whisper).isNotNull();
    }

    @Test
    public void testLoadModelNonExistentFile() {
        assertThatThrownBy(() -> new WhisperNative("src/test/resources/test-models/nonexistent.bin"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Failed to initialize WhisperNative");
    }
}
