package io.github.ferdinandmehlan.whisperspringserver.live;

import io.github.ferdinandmehlan.whisperspringserver.BaseIntegrationTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class LiveTranscriptionWebSocketHandlerTest extends BaseIntegrationTest {

    private BlockingQueue<String> receivedMessages;
    private WebSocketSession session;

    @BeforeEach
    public void setUp() throws Exception {
        receivedMessages = new ArrayBlockingQueue<>(10);
        StandardWebSocketClient client = new StandardWebSocketClient();
        TextWebSocketHandler clientHandler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession wsSession, TextMessage message) {
                receivedMessages.add(message.getPayload());
            }
        };
        String wsUrl = String.format("ws://localhost:%d/api/live", port);
        session = client.execute(clientHandler, wsUrl).get(5, TimeUnit.SECONDS);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Test
    public void testLiveTranscriptionWebSocket() throws Exception {
        // Load the sample audio bytes to mimic a real streaming client
        byte[] fullAudioBytes = Files.readAllBytes(Path.of("src/test/resources/audio/sample.wav"));
        // Strip the 44-byte WAV header; the handler expects raw 16-bit PCM
        byte[] pcmBytes = Arrays.copyOfRange(fullAudioBytes, 44, fullAudioBytes.length);
        // Define a realistic chunk size small enough to fit within Tomcat's default 8KB buffer
        int chunkSize = 4096;
        int offset = 0;

        // Loop through the raw PCM data and stream it chunk by chunk
        while (offset < pcmBytes.length) {
            int length = Math.min(chunkSize, pcmBytes.length - offset);
            byte[] chunk = Arrays.copyOfRange(pcmBytes, offset, offset + length);
            // Send current audio chunk
            session.sendMessage(new BinaryMessage(chunk));
            offset += length;
            // Mimic a short real-time delay between audio frame captures (e.g., 50 milliseconds)
            Thread.sleep(50);
        }

        // Assert: Wait for Whisper to process and reply back with text
        List<String> messages = new ArrayList<>();
        String msg = receivedMessages.poll(30, TimeUnit.SECONDS);
        do {
            messages.add(msg);
        } while ((msg = receivedMessages.poll(10, TimeUnit.SECONDS)) != null);
        String allResults = messages.stream().map(m -> "message: " + m).collect(Collectors.joining("\n"));
        assertWithFile(allResults);
    }
}
