package io.github.ferdinandmehlan.whisperspring._native.bean;

import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;

public class WhisperTranscriptionResponse extends AudioTranscriptionResponse {

    private final WhisperTranscription transcript;

    public WhisperTranscriptionResponse(WhisperTranscription transcript) {
        super(transcript);
        this.transcript = transcript;
    }

    @Override
    public WhisperTranscription getResult() {
        return this.transcript;
    }
}
