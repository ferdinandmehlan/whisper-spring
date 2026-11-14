package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring.audio.AudioDecoder;
import io.github.ferdinandmehlan.whisperspring.audio.AudioDecodingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Service for converting audio resources to wave samples suitable for Whisper transcription.
 *
 * <p>Whisper requires mono, 16kHz, 16-bit little-endian PCM. The JDK audio support is used first:
 * files in a natively readable container (WAV, AIFF, AU) are converted to the required format by
 * {@link AudioSystem}, including sample rate conversion, channel down-mixing and bit-depth
 * conversion. Everything else (MP3, M4A/AAC, OGG/Opus, FLAC, WebM, video containers, ...) is
 * delegated to a pluggable {@link AudioDecoder}, by default {@link
 * io.github.ferdinandmehlan.whisperspring.audio.FfmpegAudioDecoder}.
 */
public class WaveService {

    private static final Logger log = LoggerFactory.getLogger(WaveService.class);

    private static final float WHISPER_SAMPLE_RATE = 16000.0f;
    private static final int WHISPER_SAMPLE_SIZE_BITS = 16;
    private static final AudioFormat WHISPER_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            WHISPER_SAMPLE_RATE,
            WHISPER_SAMPLE_SIZE_BITS,
            1,
            WHISPER_SAMPLE_SIZE_BITS / 8,
            WHISPER_SAMPLE_RATE,
            false);

    private static final String UNSUPPORTED_FORMAT_MESSAGE = "Unsupported audio format. Supported natively are "
            + "WAV, AIFF and AU files in any sample rate, channel count and bit depth. Install ffmpeg or provide "
            + "an AudioDecoder bean to transcribe MP3, M4A/AAC, OGG/Opus, FLAC, WebM and video files.";

    private final AudioDecoder audioDecoder;

    /**
     * Creates a service that only uses the JDK audio support.
     */
    public WaveService() {
        this(null);
    }

    /**
     * Creates a service.
     *
     * @param audioDecoder the decoder used for formats the JDK cannot read, may be {@code null}
     */
    public WaveService(AudioDecoder audioDecoder) {
        this.audioDecoder = audioDecoder;
    }

    /**
     * Converts an audio resource to an array of float wave samples, mono and 16kHz.
     * The audio may be in any container, sample rate, channel layout and bit depth supported by the
     * configured decoders.
     *
     * @param resource the audio resource to convert
     * @return an array of float samples representing the audio waveform
     * @throws IllegalArgumentException       if the resource is null or does not exist
     * @throws AudioDecodingException         if the audio format is not supported or decoding fails
     */
    public float[] toWaveSamples(Resource resource) {
        if (resource == null || !resource.exists()) {
            throw new IllegalArgumentException("Audio file does not exist: " + resource);
        }

        try {
            return decodeWithJdk(resource);
        } catch (BuiltinDecodingUnsupportedException e) {
            log.debug("JDK audio support cannot decode {}: {}", resource.getFilename(), e.getMessage());
            if (audioDecoder == null) {
                throw new AudioDecodingException(
                        AudioDecodingException.Reason.UNSUPPORTED_FORMAT, UNSUPPORTED_FORMAT_MESSAGE, e);
            }
        }

        log.debug("Decoding {} with the external audio decoder", resource.getFilename());
        return bytesToWaveSamples(audioDecoder.decodeToPcm(resource));
    }

    /**
     * Converts raw PCM 16-bit little-endian bytes to float samples normalized to [-1, 1].
     *
     * @param pcmBytes the PCM bytes to convert
     * @return an array of float samples
     */
    public float[] bytesToWaveSamples(byte[] pcmBytes) {
        int numSamples = pcmBytes.length / 2;
        float[] samples = new float[numSamples];

        ByteBuffer buffer = ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < numSamples; i++) {
            samples[i] = buffer.getShort() / 32768.0f;
        }
        return samples;
    }

    private float[] decodeWithJdk(Resource resource) {
        try (BufferedInputStream bis = new BufferedInputStream(resource.getInputStream());
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

            AudioFormat sourceFormat = ais.getFormat();
            if (isWhisperCompatible(sourceFormat)) {
                return bytesToWaveSamples(ais.readAllBytes());
            }
            if (!AudioSystem.isConversionSupported(WHISPER_FORMAT, sourceFormat)) {
                throw new BuiltinDecodingUnsupportedException(
                        "no conversion from " + sourceFormat + " to " + WHISPER_FORMAT);
            }

            log.debug("Converting {} to mono 16kHz 16-bit PCM", sourceFormat);
            try (AudioInputStream converted = AudioSystem.getAudioInputStream(WHISPER_FORMAT, ais)) {
                return bytesToWaveSamples(converted.readAllBytes());
            }

        } catch (UnsupportedAudioFileException e) {
            throw new BuiltinDecodingUnsupportedException("unsupported container or encoding", e);
        } catch (IOException e) {
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.FAILED, "Failed to read the uploaded audio file.", e);
        }
    }

    private static boolean isWhisperCompatible(AudioFormat format) {
        return AudioFormat.Encoding.PCM_SIGNED.equals(format.getEncoding())
                && format.getChannels() == 1
                && format.getSampleRate() == WHISPER_SAMPLE_RATE
                && format.getSampleSizeInBits() == WHISPER_SAMPLE_SIZE_BITS
                && !format.isBigEndian();
    }

    /**
     * Signals that the JDK audio support cannot read or convert the given resource, so the
     * configured external decoder should be tried instead.
     */
    private static final class BuiltinDecodingUnsupportedException extends RuntimeException {

        private BuiltinDecodingUnsupportedException(String message) {
            super(message);
        }

        private BuiltinDecodingUnsupportedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
