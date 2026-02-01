package io.github.ferdinandmehlan.whisperspring;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Service for converting audio resources to wave samples suitable for Whisper transcription.
 * Supports mono, 16kHz, 16-bit little-endian PCM WAV files.
 */
@Service
public class WaveService {

    /**
     * Converts an audio resource to an array of float wave samples.
     * The audio must be a mono, 16kHz, 16-bit little-endian PCM WAV file.
     *
     * @param resource the audio resource to convert
     * @return an array of float samples representing the audio waveform
     * @throws IllegalArgumentException if the resource is null, does not exist, or is not in the supported format
     */
    public float[] toWaveSamples(Resource resource) {
        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("Audio file does not exist: " + resource);
        }

        try (BufferedInputStream bis = new BufferedInputStream(resource.getInputStream());
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

            AudioFormat format = ais.getFormat();
            validateFormat(format);
            byte[] pcmBytes = ais.readAllBytes();
            return pcm16leToFloat(pcmBytes);

        } catch (UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("Unsupported audio file format", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read audio data", e);
        }
    }

    /**
     * Converts PCM 16-bit little-endian bytes to float samples normalized to [-1, 1].
     *
     * @param pcmBytes the PCM bytes to convert
     * @return an array of float samples
     */
    private float[] pcm16leToFloat(byte[] pcmBytes) {
        int numSamples = pcmBytes.length / 2;
        float[] samples = new float[numSamples];

        ByteBuffer buffer = ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < numSamples; i++) {
            samples[i] = buffer.getShort() / 32768.0f;
        }
        return samples;
    }

    /**
     * Validates that the audio format is supported by Whisper (mono, 16kHz, 16-bit little-endian PCM).
     *
     * @param format the audio format to validate
     * @throws IllegalArgumentException if the format is not supported
     */
    private void validateFormat(AudioFormat format) {
        if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
            throw new IllegalArgumentException("Only PCM WAV supported");
        }
        if (format.getChannels() != 1) {
            throw new IllegalArgumentException("Only mono WAV supported");
        }
        if (format.getSampleRate() != 16000.0f) {
            throw new IllegalArgumentException("Expected 16kHz WAV");
        }
        if (format.getSampleSizeInBits() != 16) {
            throw new IllegalArgumentException("Expected 16-bit WAV");
        }
        if (format.isBigEndian()) {
            throw new IllegalArgumentException("Expected little-endian WAV");
        }
    }
}
