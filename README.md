# Whisper Spring

Whisper Spring integrates whisper transcription into the Spring ecosystem using Java 25's Foreign Function & Memory (FFM) API.  
The library provides autoconfigured services ready to use Whisper within Spring applications.  
It also includes a Spring server implementation that provides a ready-to-use Docker image with an OAI-like transcription API.  
The core library ships no depndent native binaries: one can build their own for their platform.  
The server images compile whisper.cpp from the vendored submodule to be self-contained.  
The library leverages FFM for direct, high-performance native interop with whisper.cpp's compiled native libraries.

The project integrates [`whisper.cpp`](https://github.com/ggml-org/whisper.cpp) as a Git submodule, 
which is an independent project providing optimized C++ implementations of Whisper inference.


## Requirements

- Java 25
- Docker


## Quick Start

1. Clone the repository including submodules:

   ```sh
   git clone --recursive https://github.com/ferdinandmehlan/whisper-spring.git
   ```

2. Build the project and start as dockerized server:

   ```sh
   ./gradlew compopseUp
   ```
   
   Or with cuda acceleration

   ```sh
   ./gradlew compopseUp -Pcuda
   ```

3. Use the Web-UI at http://localhost:8080 or transcribe an audio file directly:

   ```sh
   curl -X POST http://localhost:8080/api/transcription \
     -F "file=@/audio/sample.wav" \
     -F "responseFormat=json"
   ```
   
For detailed usage instructions, see the README files in each module.

## Modules

- [core](core/README.md) - Core library with Spring AI audio provider and native lib loader
- [server](server/README.md) - REST API server for transcription services
- [server-ui](server-ui/README.md) - Static UI build for the server module
- [test-common](test-common/README.md) - Shared test utilities


## Gradle Tasks

All important commands are made available via the Gradle build tool.  
This fully leverages gradle inputs/outputs from compiling the library, downloading a whisper model for testing, to building the docker image.
Some of the important tasks include:

| Command                      | Description                                                                                                   |
|------------------------------|---------------------------------------------------------------------------------------------------------------|
| `./gradlew composeUp`        | Start the whisper-spring-server in containerized setup (open at http://localhost:8080)                        |
| `./gradlew composeUp -Pcuda` | Start the whisper-spring-server in containerized setup with cuda acceleration (open at http://localhost:8080) |
| `./gradlew composeDown`      | Stop the container                                                                                            |
| `./gradlew clean`            | Clean build artifacts, caches, and generated files                                                            |
| `./gradlew check`            | Run all tests and formatting checks                                                                           |
| `./gradlew build`            | Execute tests and build application artifacts                                                                 |
| `./gradlew spotlessApply`    | Apply code formatting to all files                                                                            |


## Usage Examples

These are some quick examples of how this project can be used. For more details, see the module specific readmes.

### Core Library

To use whisper-spring with a spring application add the dependency:

```gradle
dependencies {
    implementation("io.github.ferdinandmehlan:whisper-spring:0.1.0")
}
```

Configure the model location and the native library location to automatically load

```yaml
whisper:
  model-path: models/ggml-tiny.bin
  native-library-path: /opt/whisper/lib
```

The core library ships no native binaries: build whisper.cpp with `-DBUILD_SHARED_LIBS=ON` and point
`native-library-path` at the resulting `libwhisper`/`ggml` libraries (file or directory). CUDA builds
need the CUDA runtime to be installed separately. See the [core README](core/README.md) for details.

Then use in your service:

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

### Server

Start the server with:

```sh
  ./gradlew :server:bootRun
```

Then transcribe via API:

```sh
  curl -X POST http://localhost:8080/api/transcription \
    -F "file=@sample.wav" \
    -F "responseFormat=json"
```

Or use the Web-UI at http://localhost:8080/

### Docker Image

Alternatively the published docker image can be used like this:

```sh
  docker run -p 8080:8080 -v ./models:/app/models ghcr.io/ferdinandmehlan/whisper-spring-server:0.1.0
```

Alternatively with CUDA acceleration :

```bash
docker run -p 8080:8080 \
  -v ./models:/app/models \
  --gpus all \
  ghcr.io/ferdinandmehlan/whisper-spring-server:0.1.0-cuda
```
Then use as above.

## License

This project is licensed under the Apache License 2.0.  
The container images contain binaries built from whisper.cpp, which is MIT licensed. The core library
itself ships no native binaries.

### Attribution

whisper.cpp  
https://github.com/ggerganov/whisper.cpp  
MIT License