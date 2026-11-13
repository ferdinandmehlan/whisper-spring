# Whisper Spring Server

The server module provides a REST API for audio transcription using Whisper models.
It offers an OpenAI-compatible transcription endpoint with comprehensive configuration options.
The container images compile whisper.cpp from the vendored submodule and ship the resulting
Linux binaries, so no native libraries have to be provided at runtime.

## Features

- **REST API**: OpenAI-compatible transcription endpoint
- **Multiple Output Formats**: Support for JSON, plain text, and SRT subtitle formats
- **Comprehensive Configuration**: Extensive parameters for fine-tuning transcription
- **OpenAPI Documentation**: Interactive API docs with Swagger UI
- **Docker Support**: Ready-to-use Docker images with native libraries compiled for the image

## Docker Image

The published Docker image includes all necessary native libraries:

```bash
docker run -p 8080:8080 -v ./models:/app/models ghcr.io/ferdinandmehlan/whisper-spring-server:0.1.0
```

Or run with CUDA

```bash
docker run -p 8080:8080 \
  -v ./models:/app/models \
  --gpus all \
  ghcr.io/ferdinandmehlan/whisper-spring-server:0.1.0-cuda
```

## Gradle Tasks

| Command                              | Description                                                 |
|--------------------------------------|-------------------------------------------------------------|
| `./gradlew :server:clean`            | Clean build artifacts, caches, and generated files          |
| `./gradlew :server:check`            | Run all tests and formatting checks                         |
| `./gradlew :server:build`            | Execute tests and build application artifacts               |
| `./gradlew :server:bootJar`          | Build the executable JAR file                               |
| `./gradlew :server:bootRun`          | Run the application locally                                 |
| `./gradlew :server:spotlessApply`    | Apply code formatting to all files                          |
| `./gradlew :server:composeUp`        | Start the server in Docker container                        |
| `./gradlew :server:composeUp -Pcuda` | Start the server in Docker container including cuda support |
| `./gradlew :server:composeDown`      | Stop the Docker container                                   |

## Local Development

Running the server locally requires a whisper.cpp build for your platform. No native binaries are
checked in: the `:buildNativeLibraries` task compiles them from the `whisper.cpp` submodule for the
platform of the JVM that runs the build and writes them to `build/native/lib`. `bootRun` depends on
it, so a single command is enough:

```sh
./gradlew :server:bootRun
```

The server will start on `http://localhost:8080`.

Or run it containerized building whisper.cpp native libs inside a container using Docker Compose:

```sh
./gradlew composeUp
```

This builds the Docker image and starts the containerized server.

Or with cuda acceleration do:
```sh
./gradlew composeUp -Pcuda
```

## Usage

```bash
curl -X POST http://localhost:8080/api/transcription \
  -F "file=@sample.wav" \
  -F "responseFormat=json"
```

### API Documentation

Interactive API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Integration

The server is built on top of the [whisper-spring](../core/README.md) core library, providing REST API access to transcription capabilities.
