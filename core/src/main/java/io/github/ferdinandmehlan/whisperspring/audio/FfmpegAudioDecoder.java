package io.github.ferdinandmehlan.whisperspring.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Decodes audio by running the {@code ffmpeg} executable in a separate process.
 *
 * <p>ffmpeg is invoked with the uploaded file as regular file argument (MP4/M4A files have their
 * index atom at the end, so they cannot be demuxed from a non-seekable pipe) and writes raw
 * {@code s16le} samples for mono 16kHz to stdout. Nothing user controlled is ever passed as an
 * argument, so no shell escaping is involved: the temporary file is created and named by this
 * class.
 *
 * <p>Invoking the executable keeps the licensing of ffmpeg out of this library: ffmpeg is not
 * linked into the JVM. It also means the decoder covers every container and codec ffmpeg knows
 * (MP3, M4A/AAC, OGG/Opus, FLAC, WebM, video containers, ...) without adding a single dependency.
 */
public class FfmpegAudioDecoder implements AudioDecoder {

    private static final Logger log = LoggerFactory.getLogger(FfmpegAudioDecoder.class);

    private static final int WHISPER_SAMPLE_RATE = 16000;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int BYTES_PER_SECOND = WHISPER_SAMPLE_RATE * BYTES_PER_SAMPLE;
    private static final int OUTPUT_READ_CHUNK_BYTES = 64 * 1024;
    private static final int MAX_STDERR_BYTES = 8 * 1024;
    private static final String DEFAULT_COMMAND = "ffmpeg";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration DEFAULT_MAX_DURATION = Duration.ofHours(2);
    private static final Duration DESTROY_TIMEOUT = Duration.ofSeconds(5);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "wav", "wave", "mp3", "m4a", "mp4", "mov", "aac", "ogg", "oga", "opus", "flac", "webm", "mkv", "wma");

    private final List<String> command;
    private final Duration timeout;
    private final Duration maxDuration;
    private final Path tempDirectory;

    /**
     * Creates a decoder that invokes {@code ffmpeg} from the PATH.
     */
    public FfmpegAudioDecoder() {
        this(List.of(DEFAULT_COMMAND), DEFAULT_TIMEOUT, DEFAULT_MAX_DURATION, null);
    }

    /**
     * Creates a decoder with default timeouts.
     *
     * @param command the command used to start ffmpeg, e.g. {@code ["ffmpeg"]} or an absolute path
     */
    public FfmpegAudioDecoder(List<String> command) {
        this(command, DEFAULT_TIMEOUT, DEFAULT_MAX_DURATION, null);
    }

    /**
     * Creates a decoder.
     *
     * @param command     the command used to start ffmpeg, e.g. {@code ["ffmpeg"]} or an absolute path
     * @param timeout     how long a single transcode may take
     * @param maxDuration the maximum amount of audio that is accepted
     * @param tempDirectory the directory for temporary files, {@code null} for the JVM default
     * @throws IllegalArgumentException if the command is null or empty, or a limit is null, zero or negative
     */
    public FfmpegAudioDecoder(List<String> command, Duration timeout, Duration maxDuration, Path tempDirectory) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("The ffmpeg command must contain at least one element");
        }
        if (timeout == null || timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("The ffmpeg timeout must be positive");
        }
        if (maxDuration == null || maxDuration.isNegative() || maxDuration.isZero()) {
            throw new IllegalArgumentException("The maximum audio duration must be positive");
        }
        this.command = List.copyOf(command);
        this.timeout = timeout;
        this.maxDuration = maxDuration;
        this.tempDirectory = tempDirectory;
    }

    @Override
    public byte[] decodeToPcm(Resource resource) {
        Path input = null;
        try {
            input = writeTempFile(resource);
            byte[] pcm = transcode(input);
            if (log.isDebugEnabled()) {
                log.debug(
                        "Decoded {} to {} samples ({} ms of 16kHz mono PCM)",
                        resource.getFilename(),
                        (long) pcm.length / BYTES_PER_SAMPLE,
                        pcm.length * 1000L / BYTES_PER_SECOND);
            }
            return pcm;
        } catch (IOException e) {
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.FAILED, "Failed to read the uploaded audio file.", e);
        } finally {
            deleteQuietly(input);
        }
    }

    /**
     * Copies the upload to a server named temporary file. ffmpeg gets a regular file instead of a
     * pipe because MP4/M4A/MOV store their index atom at the end and cannot be demuxed from a
     * non-seekable stream. The extension is whitelisted so that nothing user controlled ever ends
     * up in the argument list.
     *
     * @param resource the uploaded audio
     * @return the temporary file, to be deleted by the caller
     * @throws IOException if the upload cannot be written
     */
    private Path writeTempFile(Resource resource) throws IOException {
        String suffix = extension(resource.getFilename());
        Path file = tempDirectory == null
                ? Files.createTempFile("whisper-upload-", suffix)
                : Files.createTempFile(tempDirectory, "whisper-upload-", suffix);
        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            deleteQuietly(file);
            throw e;
        }
        return file;
    }

    private byte[] transcode(Path input) {
        Process process;
        try {
            process = new ProcessBuilder(arguments(input))
                    .redirectErrorStream(false)
                    .start();
        } catch (IOException e) {
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.DECODER_UNAVAILABLE,
                    "Failed to start the audio decoder. Install ffmpeg or provide your own AudioDecoder bean.",
                    e);
        }

        AtomicBoolean timedOut = new AtomicBoolean(false);
        Thread watchdog = Thread.ofVirtual().name("ffmpeg-watchdog").start(() -> {
            try {
                if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                    timedOut.set(true);
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        Thread stderrDrain =
                Thread.ofVirtual().name("ffmpeg-stderr").start(() -> drainQuietly(process.getErrorStream(), stderr));

        long limit = maxOutputBytes();
        byte[] pcm = null;
        boolean limitExceeded = false;
        IOException readFailure = null;
        try (InputStream out = process.getInputStream()) {
            pcm = readAtMost(out, limit);
        } catch (OutputTooLargeException e) {
            limitExceeded = true;
            process.destroyForcibly();
        } catch (IOException e) {
            readFailure = e;
        }

        joinQuietly(watchdog);
        joinQuietly(stderrDrain);
        awaitTermination(process);

        if (timedOut.get()) {
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.TIMEOUT,
                    "Audio decoding did not finish within " + timeout.toSeconds() + "s.");
        }
        if (limitExceeded) {
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.LIMIT_EXCEEDED,
                    "Audio is longer than the maximum supported duration of " + maxDuration.toMinutes() + " minutes.");
        }
        if (readFailure != null) {
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.FAILED, "Failed to read the decoded audio.", readFailure);
        }

        if (process.isAlive()) {
            process.destroyForcibly();
            throw new AudioDecodingException(AudioDecodingException.Reason.FAILED, "Audio decoding did not terminate.");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            log.debug("ffmpeg exited with {}: {}", exitCode, stderr.toString());
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.UNSUPPORTED_FORMAT,
                    "Unsupported audio format. The audio could not be decoded.");
        }
        if (pcm.length == 0) {
            log.debug("ffmpeg produced no audio: {}", stderr.toString());
            throw new AudioDecodingException(
                    AudioDecodingException.Reason.UNSUPPORTED_FORMAT,
                    "Unsupported audio format. The file does not contain a decodable audio stream.");
        }
        return pcm;
    }

    private List<String> arguments(Path input) {
        List<String> args = new ArrayList<>(command);
        args.addAll(List.of("-nostdin", "-hide_banner", "-loglevel", "error", "-threads", "1"));
        args.addAll(List.of("-i", input.toAbsolutePath().toString()));
        args.addAll(List.of("-map", "0:a:0", "-vn", "-sn", "-dn"));
        args.add("-t");
        args.add(String.valueOf(maxDuration.toMillis() / 1000.0));
        args.addAll(List.of(
                "-ac",
                String.valueOf(1),
                "-ar",
                String.valueOf(WHISPER_SAMPLE_RATE),
                "-acodec",
                "pcm_s16le",
                "-f",
                "s16le",
                "pipe:1"));
        return args;
    }

    private long maxOutputBytes() {
        return maxDuration.toSeconds() * BYTES_PER_SECOND + BYTES_PER_SECOND;
    }

    private static String extension(String filename) {
        if (filename == null) {
            return ".bin";
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return ".bin";
        }
        String extension = filename.substring(dot + 1).toLowerCase(Locale.ROOT);
        return ALLOWED_EXTENSIONS.contains(extension) ? "." + extension : ".bin";
    }

    private static byte[] readAtMost(InputStream in, long limit) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[OUTPUT_READ_CHUNK_BYTES];
        long total = 0;
        int read;
        while ((read = in.read(chunk)) != -1) {
            total += read;
            if (total > limit) {
                throw new OutputTooLargeException();
            }
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    private static void drainQuietly(InputStream in, ByteArrayOutputStream sink) {
        byte[] chunk = new byte[1024];
        long total = 0;
        try (in) {
            int read;
            while ((read = in.read(chunk)) != -1) {
                if (total < MAX_STDERR_BYTES) {
                    sink.write(chunk, 0, Math.min(read, (int) (MAX_STDERR_BYTES - total)));
                }
                total += read;
            }
        } catch (IOException e) {
            // the process was killed, nothing to report
        }
    }

    private static void joinQuietly(Thread thread) {
        try {
            thread.join(DESTROY_TIMEOUT.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void awaitTermination(Process process) {
        try {
            process.waitFor(DESTROY_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temporary audio file {}", path, e);
        }
    }

    /**
     * Signals that the decoder produced more audio than the configured maximum duration.
     */
    private static final class OutputTooLargeException extends IOException {
        private OutputTooLargeException() {
            super("Decoded audio exceeds the configured maximum duration");
        }
    }
}
