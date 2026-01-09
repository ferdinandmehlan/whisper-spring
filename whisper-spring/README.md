# Whisper Spring Core Library

The core library of Whisper Spring provides seamless integration of Whisper transcription capabilities into Spring Boot applications.  
It offers autoconfigured services, automatic native library loading,   
and a clean API for audio transcription integrating the optimized C++ implementation from whisper.cpp.

## Features

- **Auto-configuration**: Spring Boot auto-configuration for easy integration
- **Native Library Management**: Ships with precompiled native libraries for CPU and CUDA inference, 
  with automatic loading and extraction. Alternatively, supports providing self-compiled native libraries (e.g. for compiling with specific flags).
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

## Configuration

The library provides configuration through Spring Boot's `@ConfigurationProperties`. Add to your `application.yml`:

```yaml
whisper:
  libraries:
    mode: CPU  # CPU, CUDA, or Custom
    path: ./libraries  # Path to custom libraries (only used when mode is Custom)
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
        // Load a Whisper model
        WhisperCpp whisper = whisperService.loadModel("path/to/model.bin");

        // Transcribe the audio file
        FileSystemResource audioResource = new FileSystemResource(audioFile);
        return whisperService.transcribe(whisper, audioResource);
    }
}
```

### Advanced Configuration

Customize transcription parameters using `WhisperParams`:

```java
@Service
public class AdvancedTranscriptionService {

    @Autowired
    private WhisperService whisperService;

    public List<WhisperSegment> transcribeWithCustomParams(File audioFile) throws IOException {
        WhisperCpp whisper = whisperService.loadModel("models/ggml-base.bin");

        // Configure transcription parameters
        WhisperParams params = new WhisperParams(
            "en",        // language
            false,       // translate
            null,        // prompt
            0.0f,        // temperature
            0.2f,        // temperatureInc
            2,           // offsetTMs
            -1,          // offsetN
            0,           // durationMs
            -1,          // maxContext
            0,           // maxLen
            false,       // splitOnWord
            5,           // bestOf
            5,           // beamSize
            0,           // audioContext
            0.01f,       // wordThreshold
            2.4f,        // entropyThreshold
            -1.0f,       // logprobThreshold
            false,       // noTimestamps
            4,           // threads
            null,        // newSegmentCallback
            null         // progressCallback
        );

        FileSystemResource audioResource = new FileSystemResource(audioFile);
        return whisperService.transcribe(whisper, params, audioResource);
    }
}
```

### Callbacks for Real-time Processing

Implement callbacks for progress tracking and segment processing:

```java
WhisperParams params = new WhisperParams(
    // ... other parameters
    new WhisperNewSegmentCallback() {
        @Override
        public void callback(WhisperContext context, WhisperSegment segment) {
            System.out.println("New segment: " + segment.getText());
        }
    },
    new WhisperProgressCallback() {
        @Override
        public void callback(WhisperContext context, int progress) {
            System.out.println("Progress: " + progress + "%");
        }
    }
);
```

## Audio Requirements

The library and the underlying whisper models expect audio in the following format:
- **Format**: WAV (16-bit PCM)
- **Channels**: Mono (1 channel)
- **Sample Rate**: 16 kHz
- **Bit Depth**: 16-bit

The `WaveService` checks for format compatibility.
