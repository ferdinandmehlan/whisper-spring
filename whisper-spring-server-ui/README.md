# Whisper Spring Server UI

The server UI module provides a modern web interface for the Whisper Spring transcription server.
It is built with SvelteKit and offers a clean, intuitive way to interact with the Whisper Spring API.
A static export is created and shipped with the Whisper Spring Server docker image.

## Gradle Tasks

All pnpm and other tasks are wrapped by gradle to build on the projects incremental build features:

| Command                                             | Description                                        |
|-----------------------------------------------------|----------------------------------------------------|
| `./gradlew :whisper-spring-server-ui:clean`         | Clean build artifacts, caches, and generated files |
| `./gradlew :whisper-spring-server-ui:compile`       | Build the Svelte application for production        |
| `./gradlew :whisper-spring-server-ui:dev`           | Start the development server                       |
| `./gradlew :whisper-spring-server-ui:svelte-check`  | Run Svelte type checking                           |
| `./gradlew :whisper-spring-server-ui:lint`          | Run ESLint to check code quality                   |
| `./gradlew :whisper-spring-server-ui:playwright`    | Run Playwright tests                               |
| `./gradlew :whisper-spring-server-ui:playwright-ui` | Run Playwright tests with UI                       |
| `./gradlew :whisper-spring-server-ui:check`         | Run all tests and formatting checks                |
| `./gradlew :whisper-spring-server-ui:build`         | Execute tests and build application artifacts      |
| `./gradlew :whisper-spring-server-ui:format`        | Format code using Prettier                         |

## Development

Start the development server:

```sh
./gradlew :whisper-spring-server-ui:dev
```

The UI will be available at `http://localhost:5173` (default Vite dev server port).
And also start the Spring Server to provide tha API :

```sh
./gradlew :whisper-spring-server:bootRun
```

## Integration

The UI is designed to work with the [whisper-spring-server](../whisper-spring-server/README.md).
It connects to the server's REST API for all transcription operations, model management, and configuration.
