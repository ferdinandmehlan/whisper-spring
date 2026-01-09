package io.github.ferdinandmehlan.whisperspring;

import io.github.ggerganov.whispercpp.WhisperCpp;
import io.github.ggerganov.whispercpp.params.CBool;
import io.github.ggerganov.whispercpp.params.WhisperFullParams;
import io.github.ggerganov.whispercpp.params.WhisperSamplingStrategy;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping WhisperParams records to WhisperFullParams structures
 * used by the native Whisper library.
 */
@Component
public class WhisperParamsMapper {

    /**
     * Converts a WhisperParams record to a WhisperFullParams.ByValue structure.
     *
     * @param params the WhisperParams to convert
     * @param whisper the WhisperCpp instance to get default parameters from
     * @return the configured WhisperFullParams.ByValue
     */
    public WhisperFullParams.ByValue toWhisperFullParams(WhisperParams params, WhisperCpp whisper) {
        WhisperSamplingStrategy strategy = params.beamSize() > 1
                ? WhisperSamplingStrategy.WHISPER_SAMPLING_BEAM_SEARCH
                : WhisperSamplingStrategy.WHISPER_SAMPLING_GREEDY;
        WhisperFullParams.ByValue fullParams = whisper.getFullDefaultParams(strategy);

        fullParams.language = params.language();
        fullParams.translate = params.translate() ? CBool.TRUE : CBool.FALSE;
        fullParams.initial_prompt = params.prompt();
        fullParams.temperature = params.temperature();
        fullParams.temperature_inc = params.temperatureInc();
        fullParams.offset_ms = params.offsetTMs();
        fullParams.duration_ms = params.durationMs();
        fullParams.n_max_text_ctx = params.maxContext();
        fullParams.max_len = params.maxLen();
        fullParams.split_on_word = params.splitOnWord() ? CBool.TRUE : CBool.FALSE;
        fullParams.setBestOf(params.bestOf());
        fullParams.setBeamSize(params.beamSize());
        fullParams.audio_ctx = params.audioContext();
        fullParams.thold_pt = params.wordThreshold();
        fullParams.entropy_thold = params.entropyThreshold();
        fullParams.logprob_thold = params.logprobThreshold();
        fullParams.no_timestamps = params.noTimestamps() ? CBool.TRUE : CBool.FALSE;

        fullParams.n_threads = params.threads();
        fullParams.setNewSegmentCallback(params.whisperNewSegmentCallback());
        fullParams.setProgressCallback(params.whisperProgressCallback());

        return fullParams;
    }
}
