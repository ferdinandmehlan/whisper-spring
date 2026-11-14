package io.github.ferdinandmehlan.whisperspring.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Minimal stand-in for the {@code ffmpeg} executable.
 *
 * <p>Tests start this class with the same JVM that runs the test, which makes the process handling
 * of {@link FfmpegAudioDecoder} testable without ffmpeg being installed: arguments, exit codes,
 * large output and hanging processes can all be simulated deterministically.
 *
 * <p>Behaviour is configured with system properties that are passed as JVM arguments:
 * <ul>
 *   <li>{@code stub.mode} - {@code pcm} (default), {@code fail} or {@code hang}</li>
 *   <li>{@code stub.bytes} - number of bytes written to stdout in {@code pcm} mode</li>
 *   <li>{@code stub.marker} - file that receives the received arguments and the input content</li>
 * </ul>
 */
public final class FfmpegStub {

    private FfmpegStub() {}

    public static void main(String[] args) throws Exception {
        List<String> arguments = List.of(args);
        record(arguments);

        if (arguments.contains("-version")) {
            System.out.println("ffmpeg version stub");
            return;
        }

        String mode = System.getProperty("stub.mode", "pcm");
        switch (mode) {
            case "fail" -> {
                System.err.println("stub: invalid data found when processing input");
                System.exit(183);
            }
            case "hang" -> Thread.sleep(Long.MAX_VALUE);
            default -> writeStdout(Long.parseLong(System.getProperty("stub.bytes", "32000")));
        }
    }

    private static void record(List<String> arguments) throws IOException {
        String marker = System.getProperty("stub.marker");
        if (marker == null) {
            return;
        }
        Files.writeString(Path.of(marker), String.join("\n", arguments));

        int inputOption = arguments.indexOf("-i");
        if (inputOption >= 0 && inputOption + 1 < arguments.size()) {
            Path input = Path.of(arguments.get(inputOption + 1));
            if (Files.isRegularFile(input)) {
                Files.copy(input, Path.of(marker + ".input"));
            }
        }
    }

    private static void writeStdout(long bytes) throws IOException {
        OutputStream out = System.out;
        byte[] chunk = new byte[4096];
        for (int i = 0; i < chunk.length; i += 2) {
            chunk[i] = 0x00;
            chunk[i + 1] = 0x10;
        }
        long remaining = bytes;
        while (remaining > 0) {
            int length = (int) Math.min(chunk.length, remaining);
            out.write(chunk, 0, length);
            remaining -= length;
        }
        out.flush();
    }
}
