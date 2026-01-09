package io.github.ferdinandmehlan.whisperspring;

import io.github.ggerganov.whispercpp.WhisperCpp;
import io.github.ggerganov.whispercpp.bean.WhisperSegment;
import io.github.ggerganov.whispercpp.params.WhisperFullParams;
import java.io.FileNotFoundException;
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

    private final WhisperParamsMapper mapper;
    private final WaveService waveService;

    /**
     * Constructs a WhisperService with the required dependencies.
     *
     * @param mapper the WhisperParamsMapper for parameter conversion
     * @param waveService the WaveService for audio processing
     */
    public WhisperService(WhisperParamsMapper mapper, WaveService waveService) {
        this.mapper = mapper;
        this.waveService = waveService;
    }

    /**
     * Transcribes an audio file using the Whisper engine with the specified parameters.
     *
     * @param whisper the initialized WhisperCpp instance
     * @param audioFile the audio resource to transcribe (must be mono, 16kHz, 16-bit PCM WAV)
     * @return a list of WhisperSegment objects containing the transcription results
     * @throws IOException if reading the audio file fails
     */
    public List<WhisperSegment> transcribe(WhisperCpp whisper, Resource audioFile) throws IOException {
        return transcribe(whisper, new WhisperParams(), audioFile);
    }

    /**
     * Transcribes an audio file using the Whisper engine with the specified parameters.
     *
     * @param whisper the initialized WhisperCpp instance
     * @param params the transcription parameters
     * @param audioFile the audio resource to transcribe (must be mono, 16kHz, 16-bit PCM WAV)
     * @return a list of WhisperSegment objects containing the transcription results
     * @throws IOException if reading the audio file fails
     */
    public List<WhisperSegment> transcribe(WhisperCpp whisper, WhisperParams params, Resource audioFile)
            throws IOException {
        WhisperFullParams.ByValue fullParams = mapper.toWhisperFullParams(params, whisper);
        float[] samples = waveService.toWaveSamples(audioFile);
        return whisper.fullTranscribeWithTime(fullParams, samples);
    }

    /**
     * Loads a Whisper model from the specified path.
     *
     * @param model the path to the Whisper model file
     * @return an initialized WhisperCpp instance
     * @throws RuntimeException if the model cannot be loaded
     */
    public WhisperCpp loadModel(String model) {
        try {
            WhisperCpp whisper = new WhisperCpp();
            whisper.initContext(model);
            return whisper;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Model could not be initialized: " + model, e);
        }
    }
}
