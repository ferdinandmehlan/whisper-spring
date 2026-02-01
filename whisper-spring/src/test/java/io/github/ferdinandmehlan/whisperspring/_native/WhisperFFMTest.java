package io.github.ferdinandmehlan.whisperspring._native;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspring.BaseIntegrationTest;
import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

public class WhisperFFMTest extends BaseIntegrationTest {

    @Test
    public void testTranscribeJnaAndFFM() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        assertThat(audioFile.exists()).isTrue();

        WhisperNative ffmWhisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        WaveService waveService = new WaveService();
        float[] audioData = waveService.toWaveSamples(audioFile);

        List<WhisperSegment> ffmSegments = ffmWhisper.transcribe(audioData);
        String ffmResult = ffmSegments.stream().map(WhisperSegment::text).collect(Collectors.joining("\n"));

        assertThat(ffmSegments).isNotEmpty();
        System.out.println("FFM Result: " + ffmResult);

        ffmWhisper.close();
    }

    @Test
    public void testFFMLoadModel() throws IOException {
        WhisperNative whisperNative = new WhisperNative("build/resources/test/ggml-tiny.bin");
        assertThat(whisperNative).isNotNull();
        whisperNative.close();
    }
}
