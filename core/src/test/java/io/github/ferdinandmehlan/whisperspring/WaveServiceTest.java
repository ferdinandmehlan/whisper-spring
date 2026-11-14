package io.github.ferdinandmehlan.whisperspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

import io.github.ferdinandmehlan.whisperspring.audio.AudioDecodingException;
import io.github.ferdinandmehlan.whisperspringtestcommon.EnabledIfFfmpeg;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class WaveServiceTest extends BaseIntegrationTest {

    private static final int SAMPLE_RATE = 16000;
    private static final Resource SAMPLE = new FileSystemResource("src/test/resources/audio/sample.wav");

    @Autowired
    private WaveService waveService;

    @Test
    public void testToWaveSamplesHappyCase() {
        assertThat(SAMPLE.exists()).isTrue();

        float[] samples = waveService.toWaveSamples(SAMPLE);

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
    public void testToWaveSamplesConvertsStereoToMono() {
        float[] expected = waveService.toWaveSamples(SAMPLE);
        float[] samples = waveService.toWaveSamples(audioFile("sample_stereo.wav"));

        assertSameLength(samples, expected);
        assertAudible(samples);
    }

    @Test
    public void testToWaveSamplesConvertsSampleRate() {
        float[] expected = waveService.toWaveSamples(SAMPLE);
        float[] samples = waveService.toWaveSamples(audioFile("sample_44.1kHz.wav"));

        assertSameLength(samples, expected);
        assertAudible(samples);
    }

    @Test
    public void testToWaveSamplesConvertsFloatEncoding() {
        float[] expected = waveService.toWaveSamples(SAMPLE);
        float[] samples = waveService.toWaveSamples(audioFile("sample_f32.wav"));

        assertSameLength(samples, expected);
        assertAudible(samples);
    }

    @Test
    public void testToWaveSamplesBigEndianIsRejectedWithoutExternalDecoder() {
        WaveService builtinOnly = new WaveService();

        assertThatThrownBy(() -> builtinOnly.toWaveSamples(audioFile("sample_be.wav")))
                .isInstanceOf(AudioDecodingException.class)
                .hasMessageContaining("Unsupported audio format");
    }

    @Test
    public void testToWaveSamplesCompressedAudioIsRejectedWithoutExternalDecoder() {
        WaveService builtinOnly = new WaveService();

        assertThatThrownBy(() -> builtinOnly.toWaveSamples(audioFile("sample.mp3")))
                .isInstanceOf(AudioDecodingException.class)
                .hasMessageContaining("Unsupported audio format");
    }

    @Test
    @EnabledIfFfmpeg
    public void testToWaveSamplesDecodesMp3WithExternalDecoder() {
        float[] samples = waveService.toWaveSamples(audioFile("sample.mp3"));

        assertThat(samples.length).isCloseTo(25 * SAMPLE_RATE, offset(SAMPLE_RATE / 10));
        assertAudible(samples);
    }

    private static Resource audioFile(String name) {
        FileSystemResource resource = new FileSystemResource("src/test/resources/audio/" + name);
        assertThat(resource.exists()).isTrue();
        return resource;
    }

    private static void assertSameLength(float[] actual, float[] expected) {
        assertThat(actual.length).isCloseTo(expected.length, offset(expected.length / 100));
    }

    private static void assertAudible(float[] samples) {
        assertThat(samples).isNotEmpty();
        for (float sample : samples) {
            assertThat(Float.isNaN(sample)).isFalse();
        }
        float peak = 0;
        for (float sample : samples) {
            peak = Math.max(peak, Math.abs(sample));
        }
        assertThat(peak).isBetween(0.01f, 1.0f);
    }
}
