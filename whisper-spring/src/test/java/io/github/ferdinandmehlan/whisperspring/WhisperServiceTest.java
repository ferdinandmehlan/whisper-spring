package io.github.ferdinandmehlan.whisperspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ggerganov.whispercpp.WhisperCpp;
import io.github.ggerganov.whispercpp.bean.WhisperSegment;
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

        WhisperCpp whisper = whisperService.loadModel("src/test/resources/test-models/ggml-tiny.bin");
        List<WhisperSegment> segments = whisperService.transcribe(whisper, audioFile);
        String result = segments.stream().map(WhisperSegment::getSentence).collect(Collectors.joining("\n"));
        assertWithFile(result);
    }

    @Test
    public void testLoadModel() {
        WhisperCpp whisper = whisperService.loadModel("src/test/resources/test-models/ggml-tiny.bin");
        assertThat(whisper).isNotNull();
    }

    @Test
    public void testLoadModelNonExistentFile() {
        assertThatThrownBy(() -> whisperService.loadModel("src/test/resources/test-models/nonexistent.bin"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Model could not be initialized");
    }
}
