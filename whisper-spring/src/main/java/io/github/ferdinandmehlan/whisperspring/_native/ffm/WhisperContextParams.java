package io.github.ferdinandmehlan.whisperspring._native.ffm;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperContextConfig;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * Represents the whisper_context_params struct from whisper.h.
 * Used to configure context initialization with options like GPU usage and flash attention.
 *
 * C signature:
 * struct whisper_context_params {
 *     bool  use_gpu;
 *     bool  flash_attn;
 *     int   gpu_device;
 *     bool dtw_token_timestamps;
 *     enum whisper_alignment_heads_preset dtw_aheads_preset;
 *     int dtw_n_top;
 *     struct whisper_aheads dtw_aheads;
 *     size_t dtw_mem_size;
 * };
 */
public class WhisperContextParams {

    /** Memory layout for the whisper_context_params struct. */
    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
                    ValueLayout.JAVA_BOOLEAN.withName("use_gpu"),
                    ValueLayout.JAVA_BOOLEAN.withName("flash_attn"),
                    MemoryLayout.paddingLayout(2),
                    ValueLayout.JAVA_INT.withName("gpu_device"),
                    // Experimental token-level timestamps with DTW
                    ValueLayout.JAVA_BOOLEAN.withName("dtw_token_timestamps"),
                    MemoryLayout.paddingLayout(3),
                    ValueLayout.JAVA_INT.withName("dtw_aheads_preset"),
                    ValueLayout.JAVA_INT.withName("dtw_n_top"),
                    MemoryLayout.paddingLayout(4),
                    ValueLayout.JAVA_LONG.withName("dtw_aheads_n_heads"),
                    ValueLayout.ADDRESS.withName("dtw_aheads_heads"),
                    ValueLayout.JAVA_LONG.withName("dtw_mem_size"))
            .withName("whisper_context_params");

    private static final VarHandle USE_GPU = LAYOUT.varHandle(PathElement.groupElement("use_gpu"));
    private static final VarHandle FLASH_ATTN = LAYOUT.varHandle(PathElement.groupElement("flash_attn"));
    private static final VarHandle GPU_DEVICE = LAYOUT.varHandle(PathElement.groupElement("gpu_device"));

    /**
     * Allocates a whisper_context_params memory segment from a WhisperContextConfig.
     *
     * @param arena the arena to allocate memory from
     * @param config the context configuration
     * @return the allocated memory segment
     */
    public static MemorySegment allocate(Arena arena, WhisperContextConfig config) {
        MemorySegment segment = arena.allocate(LAYOUT);
        USE_GPU.set(segment, 0L, config.useGpu);
        FLASH_ATTN.set(segment, 0L, config.flashAttn);
        GPU_DEVICE.set(segment, 0L, config.gpuDevice);
        return segment;
    }
}
