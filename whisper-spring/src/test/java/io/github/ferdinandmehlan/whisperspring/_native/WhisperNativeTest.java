package io.github.ferdinandmehlan.whisperspring._native;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspring.BaseIntegrationTest;
import io.github.ferdinandmehlan.whisperspring.WaveService;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperSegment;
import io.github.ferdinandmehlan.whisperspring._native.bean.WhisperTranscribeConfig;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperEncoderBeginCallback;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperNewSegmentCallback;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperProgressCallback;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperSamplingStrategy;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

public class WhisperNativeTest extends BaseIntegrationTest {

    @Test
    public void testFFMLoadModel() throws IOException {
        WhisperNative whisperNative = new WhisperNative("build/resources/test/ggml-tiny.bin");
        assertThat(whisperNative).isNotNull();
    }

    @Test
    public void testTranscribe() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        WhisperNative ffmWhisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        WaveService waveService = new WaveService();
        float[] audioData = waveService.toWaveSamples(audioFile);

        List<WhisperSegment> segments = ffmWhisper.transcribe(audioData);
        String result = segments.stream().map(WhisperSegment::text).collect(Collectors.joining("\n"));
        assertWithFile(result);
    }

    @Test
    public void testTranscribeWithConfig() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        WhisperNative whisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        WaveService waveService = new WaveService();
        float[] audioData = waveService.toWaveSamples(audioFile);

        WhisperTranscribeConfig config = new WhisperTranscribeConfig();
        config.strategy = WhisperSamplingStrategy.WHISPER_SAMPLING_GREEDY;
        config.nThreads = 4;
        config.nMaxTextCtx = 16384;
        config.offsetMs = 0;
        config.durationMs = 0;
        config.translate = false;
        config.noContext = true;
        config.noTimestamps = false;
        config.singleSegment = false;
        config.printSpecial = false;
        config.printProgress = false;
        config.printRealtime = false;
        config.printTimestamps = true;
        config.tokenTimestamps = false;
        config.tholdPt = 0.01f;
        config.tholdPtsum = 0.01f;
        config.maxLen = 0;
        config.splitOnWord = false;
        config.maxTokens = 0;
        config.debugMode = false;
        config.audioCtx = 0;
        config.tdrzEnable = false;
        config.initialPrompt = null;
        config.carryInitialPrompt = false;
        config.promptTokens = null;
        config.language = "en";
        config.detectLanguage = false;
        config.suppressBlank = true;
        config.suppressNst = false;
        config.temperature = 0.0f;
        config.maxInitialTs = 1.0f;
        config.lengthPenalty = -1.0f;
        config.temperatureInc = 0.2f;
        config.entropyThold = 2.4f;
        config.logprobThold = -1.0f;
        config.noSpeechThold = 0.6f;
        config.greedyBestOf = 5;
        config.beamSize = 5;
        config.patience = -1.0f;

        List<WhisperSegment> segments = whisper.transcribe(audioData, config);
        String result = segments.stream().map(WhisperSegment::text).collect(Collectors.joining("\n"));
        assertWithFile(result);
    }

    @Test
    public void testTranscribeWithNewSegmentCallback() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        WhisperNative whisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        WaveService waveService = new WaveService();
        float[] audioData = waveService.toWaveSamples(audioFile);

        List<String> segmentTexts = new ArrayList<>();
        WhisperTranscribeConfig config = new WhisperTranscribeConfig();
        config.newSegmentCallback = new WhisperNewSegmentCallback() {
            private int segmentCount = 0;

            @Override
            public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
                for (int i = 0; i < nNew; i++) {
                    int segmentIndex = segmentCount + i;
                    try {
                        long t0 = whisper.fullGetSegmentT0(ctx, segmentIndex);
                        long t1 = whisper.fullGetSegmentT1(ctx, segmentIndex);
                        MemorySegment textSeg = whisper.fullGetSegmentText(ctx, segmentIndex);
                        String text = textSeg.reinterpret(1000).getString(0);
                        segmentTexts.add(String.format("[%.2fs -> %.2fs]: %s", t0 / 100.0, t1 / 100.0, text));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                segmentCount += nNew;
            }
        };

        whisper.transcribe(audioData, config);
        String result = String.join("\n", segmentTexts);
        assertWithFile(result);
    }

    @Test
    public void testTranscribeWithProgressCallback() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        WhisperNative whisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        WaveService waveService = new WaveService();
        float[] audioData = waveService.toWaveSamples(audioFile);

        List<Integer> progressValues = new ArrayList<>();
        WhisperTranscribeConfig config = new WhisperTranscribeConfig();
        config.progressCallback = new WhisperProgressCallback() {
            @Override
            public void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData) {
                progressValues.add(progress);
            }
        };

        whisper.transcribe(audioData, config);
        assertThat(progressValues).containsAll(List.of(0, 100));
    }

    @Test
    public void testTranscribeWithEncoderBeginCallback() throws IOException {
        FileSystemResource audioFile = new FileSystemResource("src/test/resources/audio/sample.wav");
        WhisperNative whisper = new WhisperNative("build/resources/test/ggml-tiny.bin");
        WaveService waveService = new WaveService();
        float[] audioData = waveService.toWaveSamples(audioFile);

        AtomicInteger encoderBeginCalls = new AtomicInteger(0);
        WhisperTranscribeConfig config = new WhisperTranscribeConfig();
        config.encoderBeginCallback = new WhisperEncoderBeginCallback() {
            @Override
            public int callback(MemorySegment ctx, MemorySegment state, MemorySegment userData) {
                encoderBeginCalls.incrementAndGet();
                return 1;
            }
        };
        whisper.transcribe(audioData, config);

        assertThat(encoderBeginCalls.get()).isEqualTo(1);
    }
}
