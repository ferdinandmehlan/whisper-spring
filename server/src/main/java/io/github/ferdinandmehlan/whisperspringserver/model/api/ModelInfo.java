package io.github.ferdinandmehlan.whisperspringserver.model.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes a single whisper model known to the server.
 *
 * <p>The {@code id} is the stable key a client sends back when it wants to use a specific model,
 * the {@code name} is what it displays. Both are derived from the configured model file, so a
 * renamed model does not change its identity.
 */
public record ModelInfo(
        @Schema(description = "Stable identifier of the model, derived from the model file name", example = "tiny")
        String id,

        @Schema(
                description = "Display name of the model, the model file name without its extension",
                example = "ggml-tiny")
        String name,

        @Schema(description = "Size of the model file in bytes, null if the file cannot be read", example = "77691713")
        Long sizeBytes) {}
