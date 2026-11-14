package io.github.ferdinandmehlan.whisperspringserver;

import static org.assertj.core.api.Assertions.assertThat;

import de.cronn.assertions.validationfile.normalization.SimpleRegexReplacement;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;
import io.github.ferdinandmehlan.whisperspringserver.transcription.api.TranscriptionResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.Container;

public class FfmpegTranscodingTest extends BaseContainerTest {

    @Test
    public void testFfmpegIsInstalledInImage() throws IOException, InterruptedException {
        Container.ExecResult result = server().execInContainer("ffmpeg", "-version");

        assertThat(result.getExitCode()).isZero();
        assertThat(result.getStdout()).contains("ffmpeg version");
        String versionLine = result.getStdout().lines().findFirst().orElseThrow();
        ValidationNormalizer versionNormalizer = ValidationNormalizer.combine(
                new SimpleRegexReplacement("ffmpeg version \\S+", "ffmpeg version [VERSION]"),
                new SimpleRegexReplacement("Copyright \\(c\\) .*", "Copyright (c) [COPYRIGHT]"));
        assertWithFileWithSuffix(versionLine, versionNormalizer, "ffmpeg_version");
    }

    @Test
    public void testTranscriptionOfM4a() {
        ResponseEntity<TranscriptionResponse> response = transcribe(audioFile("sample.m4a"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertWithFileIncludingHttpStatus(response);
        assertHeadersWithFile(response);
    }

    @Test
    public void testTranscriptionStreamingOfM4a() {
        ResponseEntity<String> response = transcribeStreaming(audioFile("sample.m4a"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertWithFileIncludingHttpStatus(response);
    }

    @Test
    public void testUndecodableAudioIsRejected(@TempDir Path tempDir) throws IOException {
        Path garbage = tempDir.resolve("not-audio.wav");
        Files.writeString(garbage, "this is definitely not an audio file");

        ResponseEntity<String> response = transcribeRaw(new FileSystemResource(garbage));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody())
                .contains("Unsupported audio format")
                .doesNotContain(tempDir.toString())
                .doesNotContain("whisper-upload");
        assertWithFileWithSuffix(response.getBody(), "response");
    }
}
