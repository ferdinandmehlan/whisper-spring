package io.github.ferdinandmehlan.whisperspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

public class WaveServiceTest extends BaseIntegrationTest {

    @Autowired
    private WaveService waveService;

    @Test
    public void testToWaveSamplesHappyCase() {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        assertThat(audioFile.exists()).isTrue();

        float[] samples = waveService.toWaveSamples(audioFile);

        assertThat(samples).isNotNull();
        assertThat(samples.length).isGreaterThan(0);
    }

    @Test
    public void testToWaveSamplesNonExistentFile() {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/nonexistent.wav");

        assertThatThrownBy(() -> waveService.toWaveSamples(audioFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Audio file does not exist");
    }

    @Test
    public void testToWaveSamplesUnsupportedFormat() {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample_f32.wav");
        assertThat(audioFile.exists()).isTrue();

        assertThatThrownBy(() -> waveService.toWaveSamples(audioFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only PCM WAV supported");
    }

    @Test
    public void testToWaveSamplesStereo() {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample_stereo.wav");
        assertThat(audioFile.exists()).isTrue();

        assertThatThrownBy(() -> waveService.toWaveSamples(audioFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only mono WAV supported");
    }

    @Test
    public void testToWaveSamplesWrongSampleRate() {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample_44.1kHz.wav");
        assertThat(audioFile.exists()).isTrue();

        assertThatThrownBy(() -> waveService.toWaveSamples(audioFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected 16kHz WAV");
    }

    @Test
    public void testToWaveSamplesBigEndian() {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample_be.wav");
        assertThat(audioFile.exists()).isTrue();

        assertThatThrownBy(() -> waveService.toWaveSamples(audioFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported audio file format");
    }
}
