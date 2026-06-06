# Whisper Spring Core Library

The core library of Whisper Spring provides seamless integration of Whisper transcription capabilities into Spring Boot applications using Java 25's Foreign Function & Memory (FFM) API.  
It offers autoconfigured services, automatic native library loading via FFM SymbolLookup, and a clean API for audio transcription integrating the optimized C++ implementation from whisper.cpp.

## Features

- **FFM Integration**: Direct native interop using Java 25's Foreign Function & Memory API for high-performance calls
- **Auto-configuration**: Spring Boot auto-configuration for easy integration
- **Native Library Management**: Ships with precompiled native libraries for CPU and CUDA inference, 
  with automatic loading through FFM's `SymbolLookup`. Alternatively, supports providing self-compiled native libraries.
- **Memory Safety**: Arena-based memory management ensures proper cleanup of native resources
- **Spring AI Audio Provider**: Ready-to-use Spring AI compatible `WhisperTranscriptionModel` provider for transcription operations
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

Add the dependency to your `build.gradle.kts`:

```gradle
dependencies {
    implementation("io.github.ferdinandmehlan:whisper-spring:0.1.0")
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

Configure the model location to automatically load

```yaml
whisper:
  model-path: models/ggml-tiny.bin
  noGpu: false
  flashAttn: true
  gpuDevice: 0
```

Then inject the `WhisperTranscriptionModel` into your Spring components:

```java
@Service
public class TranscriptionService {

    @Autowired
    private WhisperTranscriptionModel model;

    public String transcribeAudio(File audioFile) throws IOException {
        FileSystemResource audioResource = new FileSystemResource(audioFile);
        return model.transcribe(audioResource);
    }
}
```

### Advanced Configuration

Customize transcription parameters using `WhisperTranscribeConfig`:

```java
@Service
public class AdvancedTranscriptionService {

    @Autowired
    private WhisperTranscriptionModel model;

    public String transcribeWithCustomParams(File audioFile) {
        // Configure transcription parameters
        WhisperTranscriptionOptions options = new WhisperTranscriptionOptions();
        options.language = "en";
        options.translate = true;
        options.nThreads = 4;
        options.temperature = 0.0f;
        options.temperatureInc = 0.2f;

        FileSystemResource audioResource = new FileSystemResource(audioFile);
        return model.transcribe(audioResource, options);
    }
}
```

### Callbacks for Real-time Processing

Implement callbacks for progress tracking and segment processing using the sealed `WhisperCallback` interface:

```java
WhisperTranscriptionOptions options = new WhisperTranscriptionOptions();

options.newSegmentCallback = new WhisperNewSegmentCallback() {
    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int nNew, MemorySegment userData) {
        System.out.println("New segment(s) decoded: " + nNew);
    }
};

options.progressCallback = new WhisperProgressCallback() {
    @Override
    public void callback(MemorySegment ctx, MemorySegment state, int progress, MemorySegment userData) {
        System.out.println("Progress: " + progress + "%");
    }
};

options.encoderBeginCallback = new WhisperEncoderBeginCallback() {
    @Override
    public boolean callback(MemorySegment ctx, MemorySegment state, MemorySegment userData) {
        System.out.println("Encoder starting...");
        return true; // continue encoding
    }
};
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
