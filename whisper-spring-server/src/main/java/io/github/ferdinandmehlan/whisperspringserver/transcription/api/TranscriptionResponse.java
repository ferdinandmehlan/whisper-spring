package io.github.ferdinandmehlan.whisperspringserver.transcription.api;

import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Response object containing transcription results.
 * Includes both the full transcribed text and detailed segment information.
 */
public record TranscriptionResponse(
        @Schema(description = "The full transcribed text from the audio")
        String text,

        @Schema(description = "Detailed segments of the transcription with timestamps")
        List<WhisperSegment> segments) {}
