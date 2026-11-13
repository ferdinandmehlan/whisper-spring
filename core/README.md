# Whisper Spring Core Library

The core library of Whisper Spring provides seamless integration of Whisper transcription capabilities into Spring Boot applications using Java 25's Foreign Function & Memory (FFM) API.  
It offers autoconfigured services, loads the whisper.cpp native library you provide, and exposes a clean API for audio transcription.

## Features

- **FFM Integration**: Direct native interop using Java 25's Foreign Function & Memory API for high-performance calls
- **Auto-configuration**: Spring Boot auto-configuration for easy integration
- **Bring your own binaries**: No native libraries are bundled. Point `whisper.native-library-path` at a
  whisper.cpp build that matches your platform — CPU or CUDA, Linux, macOS or Windows.
- **Memory Safety**: Arena-based memory management ensures proper cleanup of native resources
- **Spring AI Audio Provider**: Ready-to-use Spring AI compatible `WhisperTranscriptionModel` provider for transcription operations
- **Flexible Configuration**: Extensive parameter customization for transcription
- **Platform Independent**: Any platform and backend that whisper.cpp can be built for
- **Spring Integration**: Full integration with Spring's dependency injection and configuration systems

## Gradle Tasks

| Command                         | Description                                        |
|---------------------------------|----------------------------------------------------|
| `./gradlew :core:clean`         | Clean build artifacts, caches, and generated files |
| `./gradlew :core:check`         | Run all tests and formatting checks                |
| `./gradlew :core:build`         | Execute tests and build application artifacts      |
| `./gradlew :core:spotlessApply` | Apply code formatting to all files                 |

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

## Native Library Setup

The core library does **not** ship native binaries. You provide a whisper.cpp build that is
compatible with the platform and CPU/GPU of your deployment; the library loads exactly the files you
point it to. There is no CPU/CUDA auto-detection and no fallback: a CUDA build is used if you point
at a CUDA build, a CPU build if you point at a CPU build.

### Building whisper.cpp

```sh
cmake -B whisper.cpp/build -S whisper.cpp \
  -DBUILD_SHARED_LIBS=ON \
  -DGGML_NATIVE=OFF \
  -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_INSTALL_PREFIX=whisper.cpp/build/install \
  -DCMAKE_INSTALL_RPATH='$ORIGIN' \
  -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON
cmake --build whisper.cpp/build --config Release -j
cmake --install whisper.cpp/build
```

### Required files

The following shared libraries have to be placed in the same directory:

| Linux                     | macOS                     | Windows            |
|---------------------------|---------------------------|--------------------|
| `libwhisper.so`           | `libwhisper.dylib`        | `whisper.dll`      |
| `libggml.so`              | `libggml.dylib`           | `ggml.dll`         |
| `libggml-base.so`         | `libggml-base.dylib`      | `ggml-base.dll`    |
| `libggml-cpu.so`          | `libggml-cpu.dylib`       | `ggml-cpu.dll`     |
| `libggml-cuda.so` (CUDA)  | —                         | `ggml-cuda.dll`    |

### Configuration

Set the native library path to either the whisper library file or the directory that contains it:

```yaml
whisper:
  model-path: models/ggml-tiny.bin
  native-library-path: /opt/whisper/lib
```

The property can also be set as the system property `whisper.native-library-path` or the environment
variable `WHISPER_NATIVE_LIBRARY_PATH`:

```sh
java -Dwhisper.native-library-path=/opt/whisper/lib \
     --enable-native-access=ALL-UNNAMED \
     -jar application.jar
```

The startup log prints the resolved library path, the platform and the version reported by
`whisper_version()`, so an incompatible library is visible immediately.

## Usage

### Basic Transcription

Configure the model location and the native library location to automatically load

```yaml
whisper:
  model-path: models/ggml-tiny.bin
  native-library-path: /opt/whisper/lib
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
