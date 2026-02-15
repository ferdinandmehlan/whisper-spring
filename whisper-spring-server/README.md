# Whisper Spring Server

The server module provides a REST API for audio transcription using Whisper models.
It offers an OpenAI-compatible transcription endpoint with comprehensive configuration options and automatic native library management.

## Features

- **REST API**: OpenAI-compatible transcription endpoint
- **Multiple Output Formats**: Support for JSON, plain text, and SRT subtitle formats
- **Comprehensive Configuration**: Extensive parameters for fine-tuning transcription
- **OpenAPI Documentation**: Interactive API docs with Swagger UI
- **Docker Support**: Ready-to-use Docker image with native libraries

## Docker Image

The published Docker image includes all necessary native libraries:

```bash
docker run -p 8080:8080 -v ./models:/app/models ghcr.io/ferdinandmehlan/whisper-spring-server:0.1.0
```

### With CUDA

To enable CUDA acceleration in the Docker container:

```bash
docker run --gpus all -p 8080:8080 \
  -v ./models:/app/models \
  ghcr.io/ferdinandmehlan/whisper-spring-server:0.1.0
```

**Note**: Requires NVIDIA Docker runtime and CUDA-compatible GPU.

## Gradle Tasks

| Command                                          | Description                                        |
|--------------------------------------------------|----------------------------------------------------|
| `./gradlew :whisper-spring-server:clean`         | Clean build artifacts, caches, and generated files |
| `./gradlew :whisper-spring-server:check`         | Run all tests and formatting checks                |
| `./gradlew :whisper-spring-server:build`         | Execute tests and build application artifacts      |
| `./gradlew :whisper-spring-server:bootJar`       | Build the executable JAR file                      |
| `./gradlew :whisper-spring-server:bootRun`       | Run the application locally                        |
| `./gradlew :whisper-spring-server:spotlessApply` | Apply code formatting to all files                 |
| `./gradlew :whisper-spring-server:composeUp`     | Start the server in Docker container               |
| `./gradlew :whisper-spring-server:composeDown`   | Stop the Docker container                          |

## Local Development

Build and run the server locally:

```sh
./gradlew :whisper-spring-server:bootRun
```

The server will start on `http://localhost:8080`.

Or run it containerized using Docker Compose:

```sh
./gradlew composeUp
```

This builds the Docker image and starts the containerized server.

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

The server is built on top of the [whisper-spring](../whisper-spring/README.md) core library, providing REST API access to transcription capabilities.
