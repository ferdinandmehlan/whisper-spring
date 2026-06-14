package io.github.ferdinandmehlan.whisperspringserver.config;

import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspringserver.live.LiveTranscriptionWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WhisperTranscriptionModel model;
    private final WaveService waveService;

    public WebSocketConfig(WhisperTranscriptionModel model, WaveService waveService) {
        this.model = model;
        this.waveService = waveService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                liveTranscriptionWebSocketHandler(), LiveTranscriptionWebSocketHandler.LIVE_TRANSCRIPTION_URL);
    }

    @Bean
    public LiveTranscriptionWebSocketHandler liveTranscriptionWebSocketHandler() {
        return new LiveTranscriptionWebSocketHandler(model, waveService);
    }
}
