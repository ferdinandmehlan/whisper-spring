package io.github.ferdinandmehlan.whisperspringserver.inference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request object for audio transcription inference.
 * Contains all parameters for configuring the Whisper transcription process.
 */
public record InferenceRequest(
        // File
        @Schema(description = "Audio file to transcribe") MultipartFile file,
        // Base Settings
        @Schema(description = "Spoken language ('auto' for auto-detect)", defaultValue = "auto") String language,
        @Schema(description = "Translate from source language to English", defaultValue = "false") Boolean translate,
        @Schema(description = "Initial prompt") @Size(max = 500) String prompt,
        @Schema(description = "Sampling temperature between 0.0 and 1.0", defaultValue = "0.0")
                @DecimalMin("0.0") @DecimalMax("1.0") Float temperature,
        @Schema(description = "Temperature increment between 0.0 and 1.0", defaultValue = "0.2")
                @DecimalMin("0.0") @DecimalMax("1.0") Float temperatureInc,
        @Schema(description = "Response format", defaultValue = "json") ResponseFormat responseFormat,
        // Additional Settings
        @Schema(description = "Time offset in milliseconds", defaultValue = "0") @Min(0) Integer offsetTMs,
        @Schema(description = "Segment index offset", defaultValue = "0") @Min(0) Integer offsetN,
        @Schema(description = "Duration of audio to process in milliseconds (0 = all)", defaultValue = "0") @Min(0) Integer durationMs,
        @Schema(description = "Maximum number of text context tokens to store (-1 = unlimited)", defaultValue = "-1")
                @Min(-1) Integer maxContext,
        @Schema(description = "Maximum segment length in characters", defaultValue = "0") @Min(0) @Max(100) Integer maxLen,
        @Schema(description = "Split on word rather than on token", defaultValue = "false") Boolean splitOnWord,
        @Schema(description = "Number of best candidates to keep", defaultValue = "2") @Min(1) @Max(8) Integer bestOf,
        @Schema(description = "Beam size for beam search (-1 = greedy)", defaultValue = "-1") @Min(-1) @Max(8) Integer beamSize,
        @Schema(description = "Audio context size (0 = all)", defaultValue = "0") @Min(0) Integer audioContext,
        @Schema(description = "Word timestamp probability threshold", defaultValue = "0.01")
                @DecimalMin("0.0") @DecimalMax("1.0") Float wordThreshold,
        @Schema(description = "Entropy threshold for decoder fail", defaultValue = "2.4") @DecimalMin("0.0") Float entropyThreshold,
        @Schema(description = "Log probability threshold for decoder fail", defaultValue = "-1.0")
                Float logprobThreshold,
        @Schema(description = "Do not include timestamps in output", defaultValue = "false") Boolean noTimestamps) {

    public InferenceRequest {
        language = Objects.requireNonNullElse(language, "auto");
        translate = Objects.requireNonNullElse(translate, false);
        temperature = Objects.requireNonNullElse(temperature, 0.0f);
        temperatureInc = Objects.requireNonNullElse(temperatureInc, 0.2f);
        responseFormat = Objects.requireNonNullElse(responseFormat, ResponseFormat.JSON);

        offsetTMs = Objects.requireNonNullElse(offsetTMs, 0);
        offsetN = Objects.requireNonNullElse(offsetN, 0);
        durationMs = Objects.requireNonNullElse(durationMs, 0);
        maxContext = Objects.requireNonNullElse(maxContext, 1);
        maxLen = Objects.requireNonNullElse(maxLen, 0);
        splitOnWord = Objects.requireNonNullElse(splitOnWord, false);
        bestOf = Objects.requireNonNullElse(bestOf, 2);
        beamSize = Objects.requireNonNullElse(beamSize, -1);
        audioContext = Objects.requireNonNullElse(audioContext, 0);
        wordThreshold = Objects.requireNonNullElse(wordThreshold, 0.01f);
        entropyThreshold = Objects.requireNonNullElse(entropyThreshold, 2.4f);
        logprobThreshold = Objects.requireNonNullElse(logprobThreshold, -1.0f);
        noTimestamps = Objects.requireNonNullElse(noTimestamps, false);
    }

    /**
     * Validates that the uploaded file is not empty.
     *
     * @return true if file exists and is not empty, false otherwise
     */
    @AssertTrue(message = "File must not be empty") public boolean isFileNotEmpty() {
        return file != null && !file.isEmpty();
    }
}
