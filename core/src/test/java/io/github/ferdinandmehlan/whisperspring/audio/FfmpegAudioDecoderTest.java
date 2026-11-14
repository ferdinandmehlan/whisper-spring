package io.github.ferdinandmehlan.whisperspring.audio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * Tests the process handling of {@link FfmpegAudioDecoder} with a Java based ffmpeg stand-in, so
 * that no ffmpeg installation is required and failure modes such as a hanging process can be
 * exercised.
 */
class FfmpegAudioDecoderTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final Duration MAX_DURATION = Duration.ofHours(2);

    @TempDir
    Path tempDir;

    @TempDir
    Path markerDir;

    @Test
    void decodesToPcmAndRemovesTheTemporaryFile() {
        FfmpegAudioDecoder decoder = decoder("stub.bytes=2000");

        byte[] decoded = decoder.decodeToPcm(audioResource(new byte[] {1, 2}, "upload.mp3"));

        assertThat(decoded).hasSize(2000).containsExactly(pcmOfLength(2000));
        assertThat(tempFiles()).isEmpty();
    }

    @Test
    void passesTheUploadAsSeekableFileAndRequestsRawMonoPcm() throws IOException {
        FfmpegAudioDecoder decoder = decoder();

        decoder.decodeToPcm(audioResource(new byte[] {1, 2, 3, 4}, "upload.mp3"));

        List<String> arguments = markerLines();
        assertThat(arguments)
                .containsSequence("-nostdin", "-hide_banner", "-loglevel", "error", "-threads", "1")
                .containsSequence("-map", "0:a:0", "-vn", "-sn", "-dn")
                .containsSequence("-ac", "1", "-ar", "16000", "-acodec", "pcm_s16le", "-f", "s16le", "pipe:1")
                .contains("-i");
        String input = arguments.get(arguments.indexOf("-i") + 1);
        assertThat(input).startsWith(tempDir.toAbsolutePath().toString()).endsWith(".mp3");
    }

    @Test
    void copiesTheUploadToDiskWithAWhitelistedExtension() throws IOException {
        FfmpegAudioDecoder decoder = decoder();
        byte[] content = "payload".getBytes(StandardCharsets.UTF_8);

        decoder.decodeToPcm(audioResource(content, "upload.m4a"));

        List<String> arguments = markerLines();
        Path input = Path.of(arguments.get(arguments.indexOf("-i") + 1));
        assertThat(input.getFileName().toString()).startsWith("whisper-upload-").endsWith(".m4a");
        assertThat(Files.readAllBytes(Path.of(markerPath() + ".input"))).containsExactly(content);
    }

    @Test
    void reportsUnavailableWhenTheExecutableCannotBeStarted() {
        FfmpegAudioDecoder decoder = new FfmpegAudioDecoder(
                List.of(tempDir.resolve("does-not-exist").toString()), TIMEOUT, MAX_DURATION, tempDir);

        assertThatThrownBy(() -> decoder.decodeToPcm(audioResource(new byte[2], "upload.wav")))
                .isInstanceOf(AudioDecodingException.class)
                .extracting(e -> ((AudioDecodingException) e).getReason())
                .isEqualTo(AudioDecodingException.Reason.DECODER_UNAVAILABLE);
    }

    @Test
    void reportsUnsupportedFormatWhenTheDecoderFails() {
        FfmpegAudioDecoder decoder = decoder("stub.mode=fail");

        assertThatThrownBy(() -> decoder.decodeToPcm(audioResource(new byte[2], "upload.wav")))
                .isInstanceOf(AudioDecodingException.class)
                .hasMessageContaining("Unsupported audio format")
                .extracting(e -> ((AudioDecodingException) e).getReason())
                .isEqualTo(AudioDecodingException.Reason.UNSUPPORTED_FORMAT);
        assertThat(tempFiles()).isEmpty();
    }

    @Test
    void rejectsAudioThatExceedsTheMaximumDuration() {
        FfmpegAudioDecoder decoder =
                decoder(Duration.ofSeconds(30), Duration.ofSeconds(1), List.of("stub.bytes=128000"));

        assertThatThrownBy(() -> decoder.decodeToPcm(audioResource(new byte[2], "upload.wav")))
                .isInstanceOf(AudioDecodingException.class)
                .extracting(e -> ((AudioDecodingException) e).getReason())
                .isEqualTo(AudioDecodingException.Reason.LIMIT_EXCEEDED);
        assertThat(tempFiles()).isEmpty();
    }

    @Test
    void killsTheDecoderWhenItDoesNotFinishInTime() {
        FfmpegAudioDecoder decoder = decoder(Duration.ofSeconds(1), MAX_DURATION, List.of("stub.mode=hang"));

        assertThatThrownBy(() -> decoder.decodeToPcm(audioResource(new byte[2], "upload.wav")))
                .isInstanceOf(AudioDecodingException.class)
                .extracting(e -> ((AudioDecodingException) e).getReason())
                .isEqualTo(AudioDecodingException.Reason.TIMEOUT);
        assertThat(tempFiles()).isEmpty();
    }

    private FfmpegAudioDecoder decoder(String... options) {
        return decoder(TIMEOUT, MAX_DURATION, List.of(options));
    }

    private FfmpegAudioDecoder decoder(Duration timeout, Duration maxDuration, List<String> options) {
        List<String> command = new ArrayList<>();
        command.add(ProcessHandle.current().info().command().orElseThrow());
        options.forEach(option -> command.add("-D" + option));
        command.add("-Dstub.marker=" + markerPath());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(FfmpegStub.class.getName());
        return new FfmpegAudioDecoder(command, timeout, maxDuration, tempDir);
    }

    private Path markerPath() {
        return markerDir.resolve("arguments.txt");
    }

    private List<String> markerLines() throws IOException {
        return Files.readAllLines(markerPath());
    }

    private List<Path> tempFiles() {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.toList();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] pcmOfLength(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < bytes.length; i += 2) {
            bytes[i] = 0x00;
            bytes[i + 1] = 0x10;
        }
        return bytes;
    }

    private static Resource audioResource(byte[] content, String filename) {
        return new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
