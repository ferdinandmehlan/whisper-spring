package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring.WhisperParams;
import io.github.ferdinandmehlan.whisperspring.WhisperService;
import io.github.ggerganov.whispercpp.WhisperCpp;
import io.github.ggerganov.whispercpp.bean.WhisperSegment;
import io.github.ggerganov.whispercpp.params.WhisperContextParams;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Main CLI command for Whisper transcription using Picocli.
 * Provides command-line interface for audio transcription with various options.
 */
@Command(name = "whisper", mixinStandardHelpOptions = true)
public class WhisperCliCommand implements Runnable {

    private final WhisperService whisperService;
    private final PrintStream out;
    private final PrintStream err;

    /**
     * Constructs a WhisperCliCommand with default output streams.
     *
     * @param whisperService the service for audio transcription
     */
    public WhisperCliCommand(WhisperService whisperService) {
        this(whisperService, System.out, System.err);
    }

    /**
     * Constructs a WhisperCliCommand with specified output streams.
     *
     * @param whisperService the service for audio transcription
     * @param out the output stream for results
     * @param err the error stream for messages
     */
    public WhisperCliCommand(WhisperService whisperService, PrintStream out, PrintStream err) {
        this.whisperService = whisperService;
        this.out = out;
        this.err = err;
    }

    // Basic options
    @Option(
            names = {"-t", "--threads"},
            description = "number of threads to use during computation")
    int threads = 4;

    @Option(
            names = {"-ot", "--offset-t"},
            description = "time offset in milliseconds")
    int offsetT = 0;

    @Option(
            names = {"-on", "--offset-n"},
            description = "segment index offset")
    int offsetN = 0;

    @Option(
            names = {"-d", "--duration"},
            description = "duration of audio to process in milliseconds")
    int duration = 0;

    // Decoding options
    @Option(
            names = {"-mc", "--max-context"},
            description = "maximum number of text context tokens to store")
    int maxContext = -1;

    @Option(
            names = {"-ml", "--max-len"},
            description = "maximum segment length in characters")
    int maxLen = 0;

    @Option(
            names = {"-sow", "--split-on-word"},
            description = "split on word rather than on token")
    boolean splitOnWord = false;

    @Option(
            names = {"-bo", "--best-of"},
            description = "number of best candidates to keep")
    int bestOf = 5;

    @Option(
            names = {"-bs", "--beam-size"},
            description = "beam size for beam search")
    int beamSize = 5;

    @Option(
            names = {"-ac", "--audio-ctx"},
            description = "audio context size (0 - all)")
    int audioCtx = 0;

    // Threshold options
    @Option(
            names = {"-wt", "--word-thold"},
            description = "word timestamp probability threshold")
    float wordThreshold = 0.01f;

    @Option(
            names = {"-et", "--entropy-thold"},
            description = "entropy threshold for decoder fail")
    float entropyThreshold = 2.40f;

    @Option(
            names = {"-lpt", "--logprob-thold"},
            description = "log probability threshold for decoder fail")
    float logprobThreshold = -1.00f;

    @Option(
            names = {"-nth", "--no-speech-thold"},
            description = "no speech threshold")
    float noSpeechThold = 0.6f;

    @Option(
            names = {"-tp", "--temperature"},
            description = "The sampling temperature, between 0 and 1")
    float temperature = 0.0f;

    @Option(
            names = {"-tpi", "--temperature-inc"},
            description = "The increment of temperature, between 0 and 1")
    float temperatureInc = 0.2f;

    // Boolean flags
    @Option(
            names = {"-debug", "--debug-mode"},
            description = "enable debug mode (eg. dump log_mel)")
    boolean debugMode = false;

    @Option(
            names = {"-tr", "--translate"},
            description = "translate from source language to english")
    boolean translate = false;

    @Option(
            names = {"-di", "--diarize"},
            description = "stereo audio diarization")
    boolean diarize = false;

    @Option(
            names = {"-tdrz", "--tinydiarize"},
            description = "enable tinydiarize (requires a tdrz model)")
    boolean tinydiarize = false;

    @Option(
            names = {"-nf", "--no-fallback"},
            description = "do not use temperature fallback while decoding")
    boolean noFallback = false;

    // Output options
    @Option(
            names = {"-otxt", "--output-txt"},
            description = "output result in a text file")
    boolean outputTxt = false;

    @Option(
            names = {"-ovtt", "--output-vtt"},
            description = "output result in a vtt file")
    boolean outputVtt = false;

    @Option(
            names = {"-osrt", "--output-srt"},
            description = "output result in a srt file")
    boolean outputSrt = false;

    @Option(
            names = {"-olrc", "--output-lrc"},
            description = "output result in a lrc file")
    boolean outputLrc = false;

    @Option(
            names = {"-owts", "--output-words"},
            description = "output script for generating karaoke video")
    boolean outputWords = false;

    @Option(
            names = {"-ocsv", "--output-csv"},
            description = "output result in a CSV file")
    boolean outputCsv = false;

    @Option(
            names = {"-oj", "--output-json"},
            description = "output result in a JSON file")
    boolean outputJson = false;

    @Option(
            names = {"-ojf", "--output-json-full"},
            description = "include more information in the JSON file")
    boolean outputJsonFull = false;

    @Option(
            names = {"-of", "--output-file"},
            description = "output file path (without file extension)")
    String outputFile;

    // Print options
    @Option(
            names = {"-np", "--no-prints"},
            description = "do not print anything other than the results")
    boolean noPrints = false;

    @Option(
            names = {"-ps", "--print-special"},
            description = "print special tokens")
    boolean printSpecial = false;

    @Option(
            names = {"-pc", "--print-colors"},
            description = "print colors")
    boolean printColors = false;

    @Option(
            names = {"-pp", "--print-progress"},
            description = "print progress")
    boolean printProgress = false;

    @Option(
            names = {"-nt", "--no-timestamps"},
            description = "do not print timestamps")
    boolean noTimestamps = false;

    // Language and model options
    @Option(
            names = {"-l", "--language"},
            description = "spoken language ('auto' for auto-detect)")
    String language = "auto";

    @Option(
            names = {"--prompt"},
            description = "initial prompt (max n_text_ctx/2 tokens)")
    String prompt;

    @Option(
            names = {"--carry-initial-prompt"},
            description = "always prepend initial prompt")
    boolean carryInitialPrompt = false;

    @Option(
            names = {"-m", "--model"},
            description = "model path")
    String model = "base";

    @Option(
            names = {"-f", "--file"},
            description = "input audio file path")
    Path file;

    // Advanced options
    @Option(
            names = {"-oved", "--ov-e-device"},
            description = "the OpenVINO device used for encode inference")
    String ovEDevice = "CPU";

    @Option(
            names = {"-dtw", "--dtw"},
            description = "compute token-level timestamps")
    String dtw;

    @Option(
            names = {"-ls", "--log-score"},
            description = "log best decoder scores of tokens")
    boolean logScore = false;

    @Option(
            names = {"-ng", "--no-gpu"},
            description = "disable GPU")
    boolean noGpu = false;

    @Option(
            names = {"-fa", "--flash-attn"},
            description = "enable flash attention")
    boolean flashAttn = false;

    @Option(
            names = {"-sns", "--suppress-nst"},
            description = "suppress non-speech tokens")
    boolean suppressNst = false;

    @Option(
            names = {"--suppress-regex"},
            description = "regular expression matching tokens to suppress")
    String suppressRegex;

    @Option(
            names = {"--grammar"},
            description = "GBNF grammar to guide decoding")
    String grammar;

    @Option(
            names = {"--grammar-rule"},
            description = "top-level GBNF grammar rule name")
    String grammarRule;

    @Option(
            names = {"--grammar-penalty"},
            description = "scales down logits of nongrammar tokens")
    float grammarPenalty = 100.0f;

    // VAD options
    @Option(
            names = {"--vad"},
            description = "enable Voice Activity Detection (VAD)")
    boolean vad = false;

    @Option(
            names = {"-vm", "--vad-model"},
            description = "VAD model path")
    String vadModel;

    @Option(
            names = {"-vt", "--vad-threshold"},
            description = "VAD threshold for speech recognition")
    float vadThreshold = 0.5f;

    @Option(
            names = {"-vspd", "--vad-min-speech-duration-ms"},
            description = "VAD min speech duration (0.0-1.0)")
    int vadMinSpeechDurationMs = 250;

    @Option(
            names = {"-vsd", "--vad-min-silence-duration-ms"},
            description = "VAD min silence duration (to split segments)")
    int vadMinSilenceDurationMs = 100;

    @Option(
            names = {"-vmsd", "--vad-max-speech-duration-s"},
            description = "VAD max speech duration (auto-split longer)")
    float vadMaxSpeechDurationS = Float.MAX_VALUE;

    @Option(
            names = {"-vp", "--vad-speech-pad-ms"},
            description = "VAD speech padding (extend segments)")
    int vadSpeechPadMs = 30;

    @Option(
            names = {"-vo", "--vad-samples-overlap"},
            description = "VAD samples overlap (seconds between segments)")
    float vadSamplesOverlap = 0.1f;

    /**
     * Executes the transcription command and exits the application.
     */
    @Override
    public void run() {
        runInternal();
        System.exit(0);
    }

    /**
     * Executes the transcription command without exiting the application.
     */
    public void runWithoutExit() {
        runInternal();
    }

    /**
     * Internal method that performs the transcription logic.
     */
    private void runInternal() {
        if (!Files.exists(this.file)) {
            err.println("Error: Audio file does not exist: " + this.file);
            return;
        }

        try {
            WhisperCpp whisper = new WhisperCpp();
            WhisperContextParams.ByValue contextParams = whisper.getContextDefaultParams();
            contextParams.useGpu(!this.noGpu);
            contextParams.useFlashAttn(this.flashAttn);
            whisper.initContext(this.model, contextParams);

            Resource resource = new FileSystemResource(file);

            List<WhisperSegment> segments = whisperService.transcribe(whisper, toWhisperParams(), resource);
            // If realtime output is enabled, segments are already printed via callback
            // Otherwise, print the final result
            if (this.noPrints) {
                String result = segments.stream()
                        .map(WhisperSegment::getSentence)
                        .map(String::trim)
                        .collect(Collectors.joining("\n"));
                out.println(result);
            }
        } catch (Exception e) {
            err.println("Error during transcription: " + e.getMessage());
        }
    }

    /**
     * Converts command-line options to WhisperParams object.
     *
     * @return WhisperParams configured with current command options
     */
    protected WhisperParams toWhisperParams() {
        WhisperNewSegmentPrinter whisperNewSegmentPrinter = !this.noPrints
                ? new WhisperNewSegmentPrinter(this.noTimestamps, this.printColors, this.printSpecial, err)
                : null;

        WhisperProgressPrinter whisperProgressPrinter = this.printProgress ? new WhisperProgressPrinter(err) : null;

        return new WhisperParams(
                this.language,
                this.translate,
                this.prompt,
                this.temperature,
                this.temperatureInc,
                this.offsetT,
                this.offsetN,
                this.duration,
                this.maxContext,
                this.maxLen,
                this.splitOnWord,
                this.bestOf,
                this.beamSize,
                this.audioCtx,
                this.wordThreshold,
                this.entropyThreshold,
                this.logprobThreshold,
                this.noTimestamps,
                this.threads,
                whisperNewSegmentPrinter,
                whisperProgressPrinter);
    }
}
