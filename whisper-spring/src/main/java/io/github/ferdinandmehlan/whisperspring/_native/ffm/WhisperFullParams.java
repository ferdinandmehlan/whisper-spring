package io.github.ferdinandmehlan.whisperspring._native.ffm;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import io.github.ferdinandmehlan.whisperspring._native.callback.CallbackHelper;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * Represents the whisper_full_params struct from whisper.h.
 * Used to configure the full transcription process.
 */
public class WhisperFullParams {

    /** Memory layout for the whisper_full_params struct. */
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("strategy"),
            ValueLayout.JAVA_INT.withName("n_threads"),
            ValueLayout.JAVA_INT.withName("n_max_text_ctx"),
            ValueLayout.JAVA_INT.withName("offset_ms"),
            ValueLayout.JAVA_INT.withName("duration_ms"),
            ValueLayout.JAVA_BOOLEAN.withName("translate"),
            ValueLayout.JAVA_BOOLEAN.withName("no_context"),
            ValueLayout.JAVA_BOOLEAN.withName("no_timestamps"),
            ValueLayout.JAVA_BOOLEAN.withName("single_segment"),
            ValueLayout.JAVA_BOOLEAN.withName("print_special"),
            ValueLayout.JAVA_BOOLEAN.withName("print_progress"),
            ValueLayout.JAVA_BOOLEAN.withName("print_realtime"),
            ValueLayout.JAVA_BOOLEAN.withName("print_timestamps"),
            ValueLayout.JAVA_BOOLEAN.withName("token_timestamps"),
            MemoryLayout.paddingLayout(3),
            ValueLayout.JAVA_FLOAT.withName("thold_pt"),
            ValueLayout.JAVA_FLOAT.withName("thold_ptsum"),
            ValueLayout.JAVA_INT.withName("max_len"),
            ValueLayout.JAVA_BOOLEAN.withName("split_on_word"),
            MemoryLayout.paddingLayout(3),
            ValueLayout.JAVA_INT.withName("max_tokens"),
            ValueLayout.JAVA_BOOLEAN.withName("debug_mode"),
            MemoryLayout.paddingLayout(3),
            ValueLayout.JAVA_INT.withName("audio_ctx"),
            ValueLayout.JAVA_BOOLEAN.withName("tdrz_enable"),
            MemoryLayout.paddingLayout(3),
            ValueLayout.ADDRESS.withName("suppress_regex"),
            ValueLayout.ADDRESS.withName("initial_prompt"),
            ValueLayout.JAVA_BOOLEAN.withName("carry_initial_prompt"),
            MemoryLayout.paddingLayout(7),
            ValueLayout.ADDRESS.withName("prompt_tokens"),
            ValueLayout.JAVA_INT.withName("prompt_n_tokens"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.ADDRESS.withName("language"),
            ValueLayout.JAVA_BOOLEAN.withName("detect_language"),
            ValueLayout.JAVA_BOOLEAN.withName("suppress_blank"),
            ValueLayout.JAVA_BOOLEAN.withName("suppress_nst"),
            MemoryLayout.paddingLayout(1), // Align float to 4 bytes
            ValueLayout.JAVA_FLOAT.withName("temperature"),
            ValueLayout.JAVA_FLOAT.withName("max_initial_ts"),
            ValueLayout.JAVA_FLOAT.withName("length_penalty"),
            ValueLayout.JAVA_FLOAT.withName("temperature_inc"),
            ValueLayout.JAVA_FLOAT.withName("entropy_thold"),
            ValueLayout.JAVA_FLOAT.withName("logprob_thold"),
            ValueLayout.JAVA_FLOAT.withName("no_speech_thold"),
            MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("best_of")).withName("greedy"),
            MemoryLayout.structLayout(
                            ValueLayout.JAVA_INT.withName("beam_size"), ValueLayout.JAVA_FLOAT.withName("patience"))
                    .withName("beam_search"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.ADDRESS.withName("new_segment_callback"),
            ValueLayout.ADDRESS.withName("new_segment_callback_user_data"),
            ValueLayout.ADDRESS.withName("progress_callback"),
            ValueLayout.ADDRESS.withName("progress_callback_user_data"),
            ValueLayout.ADDRESS.withName("encoder_begin_callback"),
            ValueLayout.ADDRESS.withName("encoder_begin_callback_user_data"),
            ValueLayout.ADDRESS.withName("abort_callback"),
            ValueLayout.ADDRESS.withName("abort_callback_user_data"),
            ValueLayout.ADDRESS.withName("logits_filter_callback"),
            ValueLayout.ADDRESS.withName("logits_filter_callback_user_data"),
            ValueLayout.ADDRESS.withName("grammar_rules"),
            ValueLayout.JAVA_LONG.withName("n_grammar_rules"),
            ValueLayout.JAVA_LONG.withName("i_start_rule"),
            ValueLayout.JAVA_FLOAT.withName("grammar_penalty"),
            ValueLayout.JAVA_BOOLEAN.withName("vad"),
            MemoryLayout.paddingLayout(3),
            ValueLayout.ADDRESS.withName("vad_model_path"),
            WhisperVadParams.LAYOUT.withName("vad_params"));

    private static final VarHandle STRATEGY = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("strategy"));
    private static final VarHandle N_THREADS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("n_threads"));
    private static final VarHandle N_MAX_TEXT_CTX =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("n_max_text_ctx"));
    private static final VarHandle OFFSET_MS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("offset_ms"));
    private static final VarHandle DURATION_MS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("duration_ms"));
    private static final VarHandle TRANSLATE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("translate"));
    private static final VarHandle NO_CONTEXT = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("no_context"));
    private static final VarHandle NO_TIMESTAMPS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("no_timestamps"));
    private static final VarHandle SINGLE_SEGMENT =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("single_segment"));
    private static final VarHandle PRINT_SPECIAL =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("print_special"));
    private static final VarHandle PRINT_PROGRESS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("print_progress"));
    private static final VarHandle PRINT_REALTIME =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("print_realtime"));
    private static final VarHandle PRINT_TIMESTAMPS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("print_timestamps"));
    private static final VarHandle TOKEN_TIMESTAMPS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("token_timestamps"));
    private static final VarHandle THOLD_PT = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("thold_pt"));
    private static final VarHandle THOLD_PTSUM = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("thold_ptsum"));
    private static final VarHandle MAX_LEN = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("max_len"));
    private static final VarHandle SPLIT_ON_WORD =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("split_on_word"));
    private static final VarHandle MAX_TOKENS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("max_tokens"));
    private static final VarHandle DEBUG_MODE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("debug_mode"));
    private static final VarHandle AUDIO_CTX = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("audio_ctx"));
    private static final VarHandle TDRZ_ENABLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("tdrz_enable"));
    private static final VarHandle INITIAL_PROMPT =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("initial_prompt"));
    private static final VarHandle CARRY_INITIAL_PROMPT =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("carry_initial_prompt"));
    private static final VarHandle PROMPT_TOKENS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("prompt_tokens"));
    private static final VarHandle PROMPT_N_TOKENS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("prompt_n_tokens"));
    private static final VarHandle LANGUAGE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("language"));
    private static final VarHandle DETECT_LANGUAGE =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("detect_language"));
    private static final VarHandle SUPPRESS_BLANK =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("suppress_blank"));
    private static final VarHandle SUPPRESS_NST =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("suppress_nst"));
    private static final VarHandle TEMPERATURE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("temperature"));
    private static final VarHandle MAX_INITIAL_TS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("max_initial_ts"));
    private static final VarHandle LENGTH_PENALTY =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("length_penalty"));
    private static final VarHandle TEMPERATURE_INC =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("temperature_inc"));
    private static final VarHandle ENTROPY_THOLD =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("entropy_thold"));
    private static final VarHandle LOGPROB_THOLD =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("logprob_thold"));
    private static final VarHandle NO_SPEECH_THOLD =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("no_speech_thold"));
    private static final VarHandle GREEDY_BEST_OF = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("greedy"), MemoryLayout.PathElement.groupElement("best_of"));
    private static final VarHandle BEAM_SIZE = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("beam_search"), MemoryLayout.PathElement.groupElement("beam_size"));
    private static final VarHandle PATIENCE = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("beam_search"), MemoryLayout.PathElement.groupElement("patience"));
    private static final VarHandle VAD = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("vad"));
    private static final VarHandle NEW_SEGMENT_CALLBACK =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("new_segment_callback"));
    private static final VarHandle PROGRESS_CALLBACK =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("progress_callback"));
    private static final VarHandle ENCODER_BEGIN_CALLBACK =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("encoder_begin_callback"));
    private static final long VAD_PARAMS_OFFSET =
            LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("vad_params"));

    /**
     * Allocates a whisper_full_params memory segment from a WhisperTranscribeConfig.
     *
     * @param arena the arena to allocate memory from
     * @param config the transcription configuration
     * @return the allocated memory segment
     */
    public static MemorySegment allocate(Arena arena, WhisperTranscribeConfig config) {
        MemorySegment segment = arena.allocate(LAYOUT);
        STRATEGY.set(segment, 0L, config.strategy.getValue());
        N_THREADS.set(segment, 0L, config.nThreads);
        N_MAX_TEXT_CTX.set(segment, 0L, config.nMaxTextCtx);
        OFFSET_MS.set(segment, 0L, config.offsetMs);
        DURATION_MS.set(segment, 0L, config.durationMs);
        TRANSLATE.set(segment, 0L, config.translate);
        NO_CONTEXT.set(segment, 0L, config.noContext);
        NO_TIMESTAMPS.set(segment, 0L, config.noTimestamps);
        SINGLE_SEGMENT.set(segment, 0L, config.singleSegment);
        PRINT_SPECIAL.set(segment, 0L, config.printSpecial);
        PRINT_PROGRESS.set(segment, 0L, config.printProgress);
        PRINT_REALTIME.set(segment, 0L, config.printRealtime);
        PRINT_TIMESTAMPS.set(segment, 0L, config.printTimestamps);
        TOKEN_TIMESTAMPS.set(segment, 0L, config.tokenTimestamps);
        THOLD_PT.set(segment, 0L, config.tholdPt);
        THOLD_PTSUM.set(segment, 0L, config.tholdPtsum);
        MAX_LEN.set(segment, 0L, config.maxLen);
        SPLIT_ON_WORD.set(segment, 0L, config.splitOnWord);
        MAX_TOKENS.set(segment, 0L, config.maxTokens);
        DEBUG_MODE.set(segment, 0L, config.debugMode);
        AUDIO_CTX.set(segment, 0L, config.audioCtx);
        TDRZ_ENABLE.set(segment, 0L, config.tdrzEnable);

        if (config.initialPrompt != null) {
            MemorySegment promptSeg = arena.allocateFrom(config.initialPrompt);
            INITIAL_PROMPT.set(segment, 0L, promptSeg);
        }
        CARRY_INITIAL_PROMPT.set(segment, 0L, config.carryInitialPrompt);

        if (config.promptTokens != null) {
            MemorySegment tokensSeg = arena.allocate(ValueLayout.JAVA_INT, config.promptTokens.length);
            for (int i = 0; i < config.promptTokens.length; i++) {
                tokensSeg.set(ValueLayout.JAVA_INT, i * ValueLayout.JAVA_INT.byteSize(), config.promptTokens[i]);
            }
            PROMPT_TOKENS.set(segment, 0L, tokensSeg);
            PROMPT_N_TOKENS.set(segment, 0L, config.promptTokens.length);
        } else {
            PROMPT_N_TOKENS.set(segment, 0L, 0);
        }

        if (config.language != null) {
            MemorySegment langSeg = arena.allocateFrom(config.language);
            LANGUAGE.set(segment, 0L, langSeg);
        }

        DETECT_LANGUAGE.set(segment, 0L, config.detectLanguage);
        SUPPRESS_BLANK.set(segment, 0L, config.suppressBlank);
        SUPPRESS_NST.set(segment, 0L, config.suppressNst);
        TEMPERATURE.set(segment, 0L, config.temperature);
        MAX_INITIAL_TS.set(segment, 0L, config.maxInitialTs);
        LENGTH_PENALTY.set(segment, 0L, config.lengthPenalty);
        TEMPERATURE_INC.set(segment, 0L, config.temperatureInc);
        ENTROPY_THOLD.set(segment, 0L, config.entropyThold);
        LOGPROB_THOLD.set(segment, 0L, config.logprobThold);
        NO_SPEECH_THOLD.set(segment, 0L, config.noSpeechThold);
        GREEDY_BEST_OF.set(segment, 0L, config.greedyBestOf);
        BEAM_SIZE.set(segment, 0L, config.beamSize);
        PATIENCE.set(segment, 0L, config.patience);
        VAD.set(segment, 0L, config.vad);

        if (config.vadConfig != null) {
            MemorySegment vadParamsSeg = WhisperVadParams.allocate(arena, config.vadConfig);
            MemorySegment.copy(vadParamsSeg, 0L, segment, VAD_PARAMS_OFFSET, WhisperVadParams.LAYOUT.byteSize());
        }

        NEW_SEGMENT_CALLBACK.set(segment, 0L, CallbackHelper.register(arena, config.newSegmentCallback));
        PROGRESS_CALLBACK.set(segment, 0L, CallbackHelper.register(arena, config.progressCallback));
        ENCODER_BEGIN_CALLBACK.set(segment, 0L, CallbackHelper.register(arena, config.encoderBeginCallback));

        return segment;
    }
}
