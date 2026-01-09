# Whisper Spring CLI

The CLI module provides a command-line interface for audio transcription using Whisper models.
It leverages the core Whisper Spring library to offer a standalone tool for transcribing audio files with extensive customization options.

## Features

- **Command-line Interface**: Easy-to-use CLI with comprehensive options for transcription
- **Model Support**: Compatible with all GGUF Whisper models (tiny, base, small, medium, large)
- **Real-time Output**: Optional progress tracking and segment-by-segment output
- **Multiple Output Formats**: Support for text, JSON, VTT, SRT, LRC, CSV, and more
- **Advanced Configuration**: Extensive parameters for fine-tuning transcription behavior
- **GPU Support**: Automatic detection and utilization of CUDA-capable GPUs

## Gradle Tasks

| Command                                       | Description                                        |
|-----------------------------------------------|----------------------------------------------------|
| `./gradlew :whisper-spring-cli:clean`         | Clean build artifacts, caches, and generated files |
| `./gradlew :whisper-spring-cli:check`         | Run all tests and formatting checks                |
| `./gradlew :whisper-spring-cli:build`         | Execute tests and build application artifacts      |
| `./gradlew :whisper-spring-cli:bootJar`       | Build the executable JAR file                      |
| `./gradlew :whisper-spring-cli:spotlessApply` | Apply code formatting to all files                 |

## Installation

Build the CLI application:

```sh
./gradlew :whisper-spring-cli:bootJar
```

This creates `whisper-spring-cli/build/libs/whisper-spring-cli-0.1.0.jar`.

## Usage

### Basic Usage

Download a Whisper model (see [models/README.md](../models/README.md) for details):

```sh
./models/download-model.sh base
```

Transcribe an audio file:

```sh
java -jar whisper-spring-cli/build/libs/whisper-spring-cli-0.1.0.jar \
  --file /path/to/audio.wav \
  --model models/ggml-base.bin
```

### Command Options

For a complete list of available configuration options, run:

```sh
java -jar whisper-spring-cli-0.1.0.jar --help
```

#### Basic Options
- `-f, --file <path>`: Input audio file path (required)
- `-m, --model <path>`: Model path (default: base)
- `-l, --language <lang>`: Spoken language ('auto' for auto-detect, default: auto)
- `-t, --threads <num>`: Number of threads to use (default: 4)

#### Output Options
- `-otxt, --output-txt`: Output result in a text file
- `-ovtt, --output-vtt`: Output result in a VTT file
- `-osrt, --output-srt`: Output result in a SRT file
- `-olrc, --output-lrc`: Output result in a LRC file
- `-ocsv, --output-csv`: Output result in a CSV file
- `-oj, --output-json`: Output result in a JSON file
- `-ojf, --output-json-full`: Include more information in the JSON file
- `-of, --output-file <path>`: Output file path (without extension)

#### Processing Options
- `-tr, --translate`: Translate from source language to English
- `-pp, --print-progress`: Print progress during transcription
- `-nt, --no-timestamps`: Do not print timestamps
- `-np, --no-prints`: Do not print anything other than the results

#### Advanced Options
- `-tp, --temperature <float>`: Sampling temperature (0.0-1.0, default: 0.0)
- `-tpi, --temperature-inc <float>`: Temperature increment (default: 0.2)
- `-bo, --best-of <num>`: Number of best candidates to keep (default: 5)
- `-bs, --beam-size <num>`: Beam size for beam search (default: 5)
- `-mc, --max-context <num>`: Maximum text context tokens (default: -1)
- `-ml, --max-len <num>`: Maximum segment length in characters (default: 0)

#### GPU and Performance
- `-ng, --no-gpu`: Disable GPU usage
- `-fa, --flash-attn`: Enable flash attention (GPU only)

### Examples

Transcribe with progress and timestamps:

```sh
java -jar whisper-spring-cli-0.1.0.jar \
  --file sample.wav \
  --model models/ggml-base.bin \
  --print-progress
```

Translate to English and output JSON:

```sh
java -jar whisper-spring-cli-0.1.0.jar \
  --file sample.wav \
  --model models/ggml-base.bin \
  --translate \
  --output-json \
  --output-file transcription
```

### Using CUDA Acceleration

To enable CUDA acceleration and use the distributed CUDA native libraries for faster transcription on NVIDIA GPUs:

```sh
WHISPER_LIBRARIES_MODE=CUDA java -jar whisper-spring-cli-0.1.0.jar \
  --file sample.wav \
  --model models/ggml-base.bin
```

**Note**: Ensure CUDA runtime libraries are installed and available in your system's library path.

## Integration

The CLI is built on top of the [whisper-spring](../whisper-spring/README.md) core library, providing the same transcription capabilities in a command-line interface.
