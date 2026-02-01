# Whisper Spring Core Library

The core library of Whisper Spring provides seamless integration of Whisper transcription capabilities into Spring Boot applications using Java 25's Foreign Function & Memory (FFM) API.  
It offers autoconfigured services, automatic native library loading via FFM SymbolLookup, and a clean API for audio transcription integrating the optimized C++ implementation from whisper.cpp.

## Features

- **FFM Integration**: Direct native interop using Java 25's Foreign Function & Memory API for high-performance calls
- **Auto-configuration**: Spring Boot auto-configuration for easy integration
- **Native Library Management**: Ships with precompiled native libraries for CPU and CUDA inference, 
  with automatic loading through FFM's `SymbolLookup`. Alternatively, supports providing self-compiled native libraries.
- **Memory Safety**: Arena-based memory management ensures proper cleanup of native resources
- **Service Layer**: Ready-to-use `WhisperService` for transcription operations
- **Flexible Configuration**: Extensive parameter customization for transcription
- **Multi-platform Support**: Supports CPU and CUDA architectures across major platforms
- **Spring Integration**: Full integration with Spring's dependency injection and configuration systems

## Gradle Tasks

| Command                                   | Description                                        |
|-------------------------------------------|----------------------------------------------------|
| `./gradlew :whisper-spring:clean`         | Clean build artifacts, caches, and generated files |
| `./gradlew :whisper-spring:check`         | Run all tests and formatting checks                |
| `./gradlew :whisper-spring:build`         | Execute tests and build application artifacts      |
| `./gradlew :whisper-spring:spotlessApply` | Apply code formatting to all files                 |

## Installation

Add the dependency to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.ferdinandmehlan:whisper-spring:0.1.0'
}
```

Or for Maven:

```xml
<dependency>
    <groupId>io.github.ferdinandmehlan</groupId>
    <artifactId>whisper-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Usage

### Basic Transcription

Inject the `WhisperService` into your Spring components:

```java
@Service
public class TranscriptionService {

    @Autowired
    private WhisperService whisperService;

    public List<WhisperSegment> transcribeAudio(File audioFile) throws IOException {
        // Create a WhisperNative instance (loads model via FFM)
        WhisperNative whisper = new WhisperNative("path/to/model.bin");

        // Transcribe the audio file
        FileSystemResource audioResource = new FileSystemResource(audioFile);
        return whisperService.transcribe(whisper, audioResource);
    }
}
```

### Advanced Configuration

Customize transcription parameters using `WhisperTranscribeConfig`:

```java
@Service
public class AdvancedTranscriptionService {

    @Autowired
    private WhisperService whisperService;

    public List<WhisperSegment> transcribeWithCustomParams(File audioFile) throws IOException {
        WhisperNative whisper = new WhisperNative("models/ggml-base.bin");

        // Configure transcription parameters
        WhisperTranscribeConfig config = new WhisperTranscribeConfig();
        config.language = "en";
        config.translate = true;
        config.nThreads = 4;
        config.temperature = 0.0f;
        config.temperatureInc = 0.2f;

        FileSystemResource audioResource = new FileSystemResource(audioFile);
        return whisperService.transcribe(whisper, config, audioResource);
    }
}
```

### Callbacks for Real-time Processing

Implement callbacks for progress tracking and segment processing using the sealed `WhisperCallback` interface:

```java
WhisperTranscribeConfig config = new WhisperTranscribeConfig();

config.newSegmentCallback = new WhisperNewSegmentCallback() {
    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
        System.out.println("New segment(s) decoded: " + nNew);
    }
};

config.progressCallback = new WhisperProgressCallback() {
    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData) {
        System.out.println("Progress: " + progress + "%");
    }
};

config.encoderBeginCallback = new WhisperEncoderBeginCallback() {
    @Override
    public boolean callback(MemorySegment ctx, MemorySegment state, MemorySegment userData) {
        System.out.println("Encoder starting...");
        return true; // continue encoding
    }
};
```

### Direct WhisperNative Usage

For more control, use `WhisperNative` directly with FFM-based transcription:

```java
try (WhisperNative whisper = new WhisperNative("ggml-base.bin")) {
    float[] audioSamples = loadAudioSamples("speech.wav");
    
    WhisperTranscribeConfig config = new WhisperTranscribeConfig();
    config.language = "en";
    config.printProgress = true;
    
    List<WhisperSegment> segments = whisper.transcribe(audioSamples, config);
    
    for (WhisperSegment segment : segments) {
        System.out.printf("[%.2fs -> %.2fs]: %s%n", 
            segment.start() / 100.0, segment.end() / 100.0, segment.text());
    }
}
```

## Audio Requirements

The library and the underlying whisper models expect audio in the following format:
- **Format**: WAV (16-bit PCM)
- **Channels**: Mono (1 channel)
- **Sample Rate**: 16 kHz
- **Bit Depth**: 16-bit

The `WaveService` checks for format compatibility.

## Foreign Function & Memory API

This library uses Java 25's Foreign Function & Memory (FFM) API for native interop:

- **Native Calls**: Functions are invoked via `Linker.nativeLinker()` without JNI overhead
- **Memory Management**: `Arena` instances manage native memory lifecycle automatically
- **Upcall Stubs**: Java callbacks are registered as native function pointers using `Linker.upcallStub()`
- **Symbol Lookup**: Native symbols are resolved via FFM's `SymbolLookup`

This approach provides:
- Better performance through direct native calls
- Automatic memory safety via arena-based management
- No manual JNI code generation required
- Full integration with Java's type system
