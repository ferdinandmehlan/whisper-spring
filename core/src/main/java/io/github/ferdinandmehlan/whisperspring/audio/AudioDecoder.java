package io.github.ferdinandmehlan.whisperspring.audio;

import org.springframework.core.io.Resource;

/**
 * Decodes an audio resource into the raw PCM format expected by Whisper:
 * mono, 16kHz, signed 16-bit little-endian.
 *
 * <p>Implementations are pluggable: the default is {@link FfmpegAudioDecoder}, but any bean of
 * this type can replace it. The interface intentionally returns raw bytes so that decoding can be
 * decoupled from the sample conversion in
 * {@link io.github.ferdinandmehlan.whisperspring.WaveService}.
 */
public interface AudioDecoder {

    /**
     * Decodes the given resource into mono, 16kHz, signed 16-bit little-endian PCM.
     *
     * @param resource the audio resource to decode
     * @return the raw PCM bytes
     * @throws AudioDecodingException if the audio cannot be decoded
     */
    byte[] decodeToPcm(Resource resource);
}
