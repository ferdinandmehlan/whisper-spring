package io.github.ferdinandmehlan.whisperspring._native.bean;

import org.springframework.ai.audio.transcription.AudioTranscription;

public class WhisperTranscription extends AudioTranscription {

    private WhisperTranscriptionMetadata transcriptionMetadata;

    public WhisperTranscription(String text) {
        super(text);
    }

    @Override
    public WhisperTranscriptionMetadata getMetadata() {
        return this.transcriptionMetadata != null ? this.transcriptionMetadata : WhisperTranscriptionMetadata.NULL;
    }

    public WhisperTranscription withTranscriptionMetadata(WhisperTranscriptionMetadata transcriptionMetadata) {
        this.transcriptionMetadata = transcriptionMetadata;
        return this;
    }
}
