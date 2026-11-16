package io.github.ferdinandmehlan.whisperspringserver.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response object listing the models the server can transcribe with.
 *
 * <p>The server currently loads exactly one model at startup, so {@code models} holds a single
 * entry. It is a list rather than a single object because selecting between several models is
 * planned, and a list lets that happen without breaking clients.
 */
public record ModelListResponse(
        @Schema(
                description = "Identifier of the model currently loaded into memory, null if no model is loaded",
                example = "tiny")
        String active,

        @Schema(description = "Models available to the server, empty if no model is loaded")
        List<ModelInfo> models) {}
