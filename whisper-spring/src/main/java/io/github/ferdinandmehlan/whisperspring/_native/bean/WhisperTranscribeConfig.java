package io.github.ferdinandmehlan.whisperspring._native.bean;

import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperEncoderBeginCallback;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperNewSegmentCallback;
import io.github.ferdinandmehlan.whisperspring._native.callback.WhisperProgressCallback;
import io.github.ferdinandmehlan.whisperspring._native.ffm.WhisperSamplingStrategy;

/**
 * Configuration for Whisper transcription.
 * Contains all parameters that control the transcription process.
 */
public class WhisperTranscribeConfig {

    public WhisperSamplingStrategy strategy;
    public int nThreads;
    public int nMaxTextCtx;
    public int offsetMs;
    public int durationMs;
    public boolean translate;
    public boolean noContext;
    public boolean noTimestamps;
    public boolean singleSegment;
    public boolean printSpecial;
    public boolean printProgress;
    public boolean printRealtime;
    public boolean printTimestamps;
    public boolean tokenTimestamps;
    public float tholdPt;
    public float tholdPtsum;
    public int maxLen;
    public boolean splitOnWord;
    public int maxTokens;
    public boolean debugMode;
    public int audioCtx;
    public boolean tdrzEnable;
    public String initialPrompt;
    public boolean carryInitialPrompt;
    public int[] promptTokens;
    public String language;
    public boolean detectLanguage;
    public boolean suppressBlank;
    public boolean suppressNst;
    public float temperature;
    public float maxInitialTs;
    public float lengthPenalty;
    public float temperatureInc;
    public float entropyThold;
    public float logprobThold;
    public float noSpeechThold;
    public int greedyBestOf;
    public int beamSize;
    public float patience;
    public boolean vad;
    public String vadModelPath;
    public WhisperVadConfig vadConfig;

    /**
     * Callback invoked when a new segment is decoded.
     */
    public WhisperNewSegmentCallback newSegmentCallback;

    /**
     * Callback invoked during transcription progress.
     */
    public WhisperProgressCallback progressCallback;

    /**
     * Callback invoked when encoder begins.
     */
    public WhisperEncoderBeginCallback encoderBeginCallback;

    /**
     * Creates a new WhisperTranscribeConfig with default settings.
     */
    public WhisperTranscribeConfig() {
        this.strategy = WhisperSamplingStrategy.WHISPER_SAMPLING_GREEDY;
        this.nThreads = Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors()));
        this.nMaxTextCtx = 16384;
        this.offsetMs = 0;
        this.durationMs = 0;
        this.translate = false;
        this.noContext = true;
        this.noTimestamps = false;
        this.singleSegment = false;
        this.printSpecial = false;
        this.printProgress = true;
        this.printRealtime = false;
        this.printTimestamps = true;
        this.tokenTimestamps = false;
        this.tholdPt = 0.01f;
        this.tholdPtsum = 0.01f;
        this.maxLen = 0;
        this.splitOnWord = false;
        this.maxTokens = 0;
        this.debugMode = false;
        this.audioCtx = 0;
        this.tdrzEnable = false;
        this.initialPrompt = null;
        this.carryInitialPrompt = false;
        this.promptTokens = null;
        this.language = "auto";
        this.detectLanguage = false;
        this.suppressBlank = true;
        this.suppressNst = false;
        this.temperature = 0.0f;
        this.maxInitialTs = 1.0f;
        this.lengthPenalty = -1.0f;
        this.temperatureInc = 0.2f;
        this.entropyThold = 2.4f;
        this.logprobThold = -1.0f;
        this.noSpeechThold = 0.6f;
        this.greedyBestOf = 5;
        this.beamSize = 5;
        this.patience = -1.0f;
        this.vad = false;
        this.vadModelPath = null;
        this.vadConfig = new WhisperVadConfig();
    }
}
