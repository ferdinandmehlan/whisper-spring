package io.github.ferdinandmehlan.whisperspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;

class WhisperAutoConfigurationTest {

    @Nested
    class WithModel extends BaseIntegrationTest {

        @Autowired
        private WhisperNative whisperNative;

        @Autowired
        private WhisperTranscriptionModel whisperTranscriptionModel;

        @Value("${whisper.model-path}")
        private String modelPath;

        @Test
        void testAutoConfiguredWhisperNativeBeanExists() {
            assertThat(whisperNative).isNotNull();
        }

        @Test
        void testModelPathIsConfigured() {
            assertThat(modelPath).isEqualTo("build/resources/test/ggml-tiny.bin");
        }

        @Test
        void testTranscriptionModelCanTranscribeWithoutExplicitInit() {
            FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
            assertThat(audioFile.exists()).isTrue();

            String result = whisperTranscriptionModel.transcribe(audioFile);
            assertWithFile(result);
        }
    }

    @SpringBootApplication
    static class TestApplicationWithoutModel {}

    @Nested
    @SpringBootTest(classes = WhisperAutoConfigurationTest.TestApplicationWithoutModel.class)
    class WithoutModel {

        @Autowired(required = false)
        private WhisperNative whisperNative;

        @Autowired
        private WhisperTranscriptionModel whisperTranscriptionModel;

        @Test
        void testWhisperNativeBeanIsNotCreated() {
            assertThat(whisperNative).isNull();
        }

        @Test
        void testWhisperTranscriptionModelBeanExists() {
            assertThat(whisperTranscriptionModel).isNotNull();
        }

        @Test
        void testTranscribeThrowsWhenNativeNotConfigured() {
            FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
            AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile);

            assertThatThrownBy(() -> whisperTranscriptionModel.call(prompt))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("WhisperNative has not been initialized");
        }
    }
}
