package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Service for transcribing audio files using the Whisper speech recognition engine.
 * Handles model loading and audio transcription with configurable parameters.
 */
@Service
public class WhisperService {

    private final WaveService waveService;

    /**
     * Creates a new WhisperService.
     *
     * @param waveService the wave service for audio processing
     */
    public WhisperService(WaveService waveService) {
        this.waveService = waveService;
    }

    /**
     * Transcribes an audio file using default configuration.
     *
     * @param whisper the WhisperNative instance
     * @param audioFile the audio resource to transcribe
     * @return list of transcription segments
     * @throws IOException if transcription fails
     */
    public List<WhisperSegment> transcribe(WhisperNative whisper, Resource audioFile) throws IOException {
        return transcribe(whisper, new WhisperTranscribeConfig(), audioFile);
    }

    /**
     * Transcribes an audio file with custom configuration.
     *
     * @param whisper the WhisperNative instance
     * @param config the transcription configuration
     * @param audioFile the audio resource to transcribe
     * @return list of transcription segments
     * @throws IOException if transcription fails
     */
    public List<WhisperSegment> transcribe(WhisperNative whisper, WhisperTranscribeConfig config, Resource audioFile)
            throws IOException {
        float[] audioData = waveService.toWaveSamples(audioFile);
        return whisper.transcribe(audioData, config);
    }
}
