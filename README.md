# Whisper Spring

Whisper Spring integrates whisper transcription into the Spring ecosystem using Java 25's Foreign Function & Memory (FFM) API.  
The library provides autoconfigured services ready to use Whisper within Spring applications.  
It also includes a CLI implementation and a Spring server implementation that provides a ready-to-use Docker image with an OAI-like transcription API.  
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

3. Use the Web-UI at http://localhost:8080 or transcribe an audio file directly:

   ```sh
   curl -X POST http://localhost:8080/api/transcription \
     -F "file=@/audio/sample.wav" \
     -F "responseFormat=json"
   ```
   
For detailed usage instructions, see the README files in each module.

## Modules

- [whisper-spring](whisper-spring/README.md) - Core library with services and native lib loader
- [whisper-spring-cli](whisper-spring-cli/README.md) - Command-line interface for transcription
- [whisper-spring-server](whisper-spring-server/README.md) - REST API server for transcription services
- [whisper-spring-test-common](whisper-spring-test-common/README.md) - Shared test utilities


## Gradle Tasks

All important commands are made available via the Gradle build tool.  
This fully leverages gradle inputs/outputs from compiling the library, downloading a whisper model for testing, to building the docker image.
Some of the important tasks include:

| Command                   | Description                                                                            |
|---------------------------|----------------------------------------------------------------------------------------|
| `./gradlew composeUp`     | Start the whisper-spring-server in containerized setup (open at http://localhost:8080) |
| `./gradlew composeDown`   | Stop the container                                                                     |
| `./gradlew clean`         | Clean build artifacts, caches, and generated files                                     |
| `./gradlew check`         | Run all tests and formatting checks                                                    |
| `./gradlew build`         | Execute tests and build application artifacts                                          |
| `./gradlew spotlessApply` | Apply code formatting to all files                                                     |


## Usage Examples

These are some quick examples of how this project can be used. For more details, see the module specific readmes.

### Core Library (whisper-spring)

To use whisper-spring with a spring application add the dependency:

```gradle
dependencies {
    implementation 'io.github.ferdinandmehlan:whisper-spring:0.1.0'
}
```

Then use in your service:

```java
@Service
public class MyService {

    @Autowired
    private WhisperService whisperService;

    public void transcribe() throws IOException {
        WhisperNative whisper = new WhisperNative("ggml-tiny.bin");
        List<WhisperSegment> segments = whisperService.transcribe(whisper, new FileSystemResource("sample.wav"));
    }
}
```

### CLI (whisper-spring-cli)

To use the CLI download or build it with:

```sh
  ./gradlew :whisper-spring-cli:bootJar
```

Download a Whisper model see [models/README.md](models/README.md) for details.  
Then run:

```sh
  java -jar whisper-spring-cli-0.1.0.jar --file sample.wav --model models/ggml-base.bin
```

### Server (whisper-spring-server)

Start the server with:

```sh
  ./gradlew :whisper-spring-server:bootRun
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

Then use as above.

## License

This project is licensed under the Apache License 2.0.  
It bundles binaries built from whisper.cpp, which is MIT licensed.

### Attribution

whisper.cpp  
https://github.com/ggerganov/whisper.cpp  
MIT License