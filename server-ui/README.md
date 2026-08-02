# Whisper Spring Server UI

The server UI module provides a modern web interface for the Whisper Spring transcription server.
It is built with SvelteKit and offers a clean, intuitive way to interact with the Whisper Spring API.
A static export is created and shipped with the Whisper Spring Server docker image.

## Gradle Tasks

All pnpm and other tasks are wrapped by gradle to build on the projects incremental build features:

| Command                                   | Description                                        |
|-------------------------------------------|----------------------------------------------------|
| `./gradlew :server-ui:clean`              | Clean build artifacts, caches, and generated files |
| `./gradlew :server-ui:compile`            | Build the Svelte application for production        |
| `./gradlew :server-ui:dev`                | Start the development server                       |
| `./gradlew :server-ui:check`              | Run all tests and formatting checks                |
| `./gradlew :server-ui:build`              | Execute tests and build application artifacts      |
| `./gradlew :server-ui:updateDependencies` | Update project dependencies                        |
| `./gradlew :server-ui:formatCheck`        | Check code formatting with Prettier                |
| `./gradlew :server-ui:format`             | Format code using Prettier                         |
| `./gradlew :server-ui:lintCheck`          | Run ESLint to check code quality                   |
| `./gradlew :server-ui:lint`               | Run ESLint to fix code quality issues              |
| `./gradlew :server-ui:svelteCheck`        | Run Svelte type checking                           |
| `./gradlew :server-ui:playwright`         | Run Playwright tests                               |
| `./gradlew :server-ui:playwrightUi`       | Open Playwright interactive UI                     |

## Development

Start the development server:

```sh
./gradlew :server-ui:dev
```

The UI will be available at `http://localhost:5173` (default Vite dev server port).
And also start the Spring Server to provide the API :

```sh
./gradlew :server:bootRun
```

## Integration

The UI is designed to work with the [server](../server/README.md).
It connects to the server's REST API for all transcription operations, model management, and configuration.
