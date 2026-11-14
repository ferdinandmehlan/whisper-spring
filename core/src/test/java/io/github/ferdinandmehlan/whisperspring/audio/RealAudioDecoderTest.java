package io.github.ferdinandmehlan.whisperspring.audio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import io.github.ferdinandmehlan.whisperspringtestcommon.EnabledIfFfmpeg;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Decodes real compressed fixtures with a real ffmpeg installation.
 *
 * <p>The fixtures are full-length copies of {@code sample.wav}. Nothing asserts
 * sample-exact output: resampling and dithering differ between ffmpeg builds. Instead the decoded
 * length, the absence of invalid samples and a plausible peak are verified.
 */
@EnabledIfFfmpeg
class RealAudioDecoderTest {

    private static final int SAMPLE_RATE = 16000;
    private static final int SAMPLE_SECONDS = 25;

    private final FfmpegAudioDecoder decoder = new FfmpegAudioDecoder();

    @Test
    void decodesMp3() {
        assertDecoded("sample.mp3", SAMPLE_SECONDS);
    }

    @Test
    void decodesM4a() {
        assertDecoded("sample.m4a", SAMPLE_SECONDS);
    }

    @Test
    void decodesFlac() {
        assertDecoded("sample.flac", SAMPLE_SECONDS);
    }

    @Test
    void decodesWaveThroughFfmpeg() {
        assertDecoded("sample.wav", SAMPLE_SECONDS);
    }

    private void assertDecoded(String filename, int seconds) {
        Resource file = new FileSystemResource("src/test/resources/audio/" + filename);
        assertThat(file.exists()).isTrue();

        float[] samples = toSamples(decoder.decodeToPcm(file));

        assertThat(samples.length).isCloseTo(seconds * SAMPLE_RATE, offset(SAMPLE_RATE / 10));
        assertAudible(samples);
    }

    private static float[] toSamples(byte[] pcm) {
        float[] samples = new float[pcm.length / 2];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (short) ((pcm[2 * i] & 0xff) | (pcm[2 * i + 1] << 8)) / 32768.0f;
        }
        return samples;
    }

    private static void assertAudible(float[] samples) {
        float peak = 0;
        double sumOfSquares = 0;
        for (float sample : samples) {
            assertThat(Float.isNaN(sample) || Float.isInfinite(sample)).isFalse();
            assertThat(sample).isBetween(-1.0f, 1.0f);
            peak = Math.max(peak, Math.abs(sample));
            sumOfSquares += (double) sample * sample;
        }
        double rms = Math.sqrt(sumOfSquares / samples.length);
        assertThat(peak).isBetween(0.05f, 1.0f);
        assertThat(rms).isBetween(0.001, 1.0);
    }
}
