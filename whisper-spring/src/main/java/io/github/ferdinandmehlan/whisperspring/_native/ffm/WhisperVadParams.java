package io.github.ferdinandmehlan.whisperspring._native.ffm;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperVadConfig;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * Represents the whisper_vad_params struct from whisper.h.
 * Used to configure Voice Activity Detection parameters.
 */
public class WhisperVadParams {

    /** Memory layout for the whisper_vad_params struct. */
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
                    ValueLayout.JAVA_FLOAT.withName("threshold"),
                    ValueLayout.JAVA_INT.withName("min_speech_duration_ms"),
                    ValueLayout.JAVA_INT.withName("min_silence_duration_ms"),
                    ValueLayout.JAVA_FLOAT.withName("max_speech_duration_s"),
                    ValueLayout.JAVA_INT.withName("speech_pad_ms"),
                    ValueLayout.JAVA_FLOAT.withName("samples_overlap"))
            .withName("whisper_vad_params");

    private static final VarHandle THRESHOLD = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("threshold"));
    private static final VarHandle MIN_SPEECH_DURATION_MS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("min_speech_duration_ms"));
    private static final VarHandle MIN_SILENCE_DURATION_MS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("min_silence_duration_ms"));
    private static final VarHandle MAX_SPEECH_DURATION_S =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("max_speech_duration_s"));
    private static final VarHandle SPEECH_PAD_MS =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("speech_pad_ms"));
    private static final VarHandle SAMPLES_OVERLAP =
            LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("samples_overlap"));

    /**
     * Allocates a whisper_vad_params memory segment from a WhisperVadConfig.
     *
     * @param arena the arena to allocate memory from
     * @param config the VAD configuration
     * @return the allocated memory segment
     */
    public static MemorySegment allocate(Arena arena, WhisperVadConfig config) {
        MemorySegment segment = arena.allocate(LAYOUT);
        THRESHOLD.set(segment, 0L, config.threshold);
        MIN_SPEECH_DURATION_MS.set(segment, 0L, config.minSpeechDurationMs);
        MIN_SILENCE_DURATION_MS.set(segment, 0L, config.minSilenceDurationMs);
        MAX_SPEECH_DURATION_S.set(segment, 0L, config.maxSpeechDurationS);
        SPEECH_PAD_MS.set(segment, 0L, config.speechPadMs);
        SAMPLES_OVERLAP.set(segment, 0L, config.samplesOverlap);
        return segment;
    }
}
