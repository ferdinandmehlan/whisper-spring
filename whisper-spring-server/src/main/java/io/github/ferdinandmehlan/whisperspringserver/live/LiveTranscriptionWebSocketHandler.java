package io.github.ferdinandmehlan.whisperspringserver.live;

import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscriptionOptions;
import io.github.ferdinandmehlan.whisperspring.live.LiveTranscriptionSession;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

public class LiveTranscriptionWebSocketHandler extends BinaryWebSocketHandler {

    public static final String LIVE_TRANSCRIPTION_URL = "/api/live";

    private static final Logger log = LoggerFactory.getLogger(LiveTranscriptionWebSocketHandler.class);

    private final WhisperTranscriptionModel model;
    private final WaveService waveService;
    private final ConcurrentHashMap<String, LiveTranscriptionSession> activeSessions = new ConcurrentHashMap<>();

    public LiveTranscriptionWebSocketHandler(WhisperTranscriptionModel model, WaveService waveService) {
        this.model = model;
        this.waveService = waveService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        var options = new WhisperTranscriptionOptions();
        var session = new LiveTranscriptionSession(
                webSocketSession.getId(),
                model,
                waveService,
                options,
                transcription -> {
                    String text = transcription.getOutput();
                    if (webSocketSession.isOpen() && !text.isBlank()) {
                        try {
                            webSocketSession.sendMessage(new TextMessage(text));
                            return;
                        } catch (Exception e) {
                            log.error("Failed to send transcription result for session {}", webSocketSession.getId(), e);
                        }
                    }

                    LiveTranscriptionSession s = activeSessions.remove(webSocketSession.getId());
                    if (s != null) {
                        s.close();
                    }
                });
        activeSessions.put(webSocketSession.getId(), session);
        log.info("Live transcription session started: {}", webSocketSession.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession webSocketSession, BinaryMessage message) {
        LiveTranscriptionSession session = activeSessions.get(webSocketSession.getId());
        if (session == null) {
            return;
        }
        session.appendAudio(message.getPayload().array());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) {
        LiveTranscriptionSession session = activeSessions.remove(webSocketSession.getId());
        if (session != null) {
            session.close();
            log.info("Live transcription session closed: {}. Close status: {}", webSocketSession.getId(), status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable exception) {
        log.error("Transport error on session {}", webSocketSession.getId(), exception);
    }
}
