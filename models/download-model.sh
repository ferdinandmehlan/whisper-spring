#!/bin/bash

# Downloads models from Hugging Face ggerganov/whisper.cpp repository

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Available models
MODELS="tiny tiny.en tiny-q5_1 tiny.en-q5_1 tiny-q8_0 base base.en base-q5_1 base.en-q5_1 base-q8_0 small small.en small.en-tdrz small-q5_1 small.en-q5_1 small-q8_0 medium medium.en medium-q5_0 medium.en-q5_0 medium-q8_0 large-v1 large-v2 large-v2-q5_0 large-v2-q8_0 large-v3 large-v3-q5_0 large-v3-turbo large-v3-turbo-q5_0 large-v3-turbo-q8_0"

# Colors for output
BOLD="\033[1m"
RESET='\033[0m'

# Function to display usage
usage() {
    echo "Usage: $0 <model_name>"
    echo ""
    echo "Available models:"
    echo ""
    echo "Tiny models (39 MB):"
    echo "  tiny        - Tiny model"
    echo "  tiny.en     - Tiny English-only model"
    echo "  tiny-q5_1   - Tiny model, 5-bit quantization"
    echo "  tiny.en-q5_1 - Tiny English-only, 5-bit quantization"
    echo "  tiny-q8_0   - Tiny model, 8-bit quantization"
    echo ""
    echo "Base models (74 MB):"
    echo "  base        - Base model"
    echo "  base.en     - Base English-only model"
    echo "  base-q5_1   - Base model, 5-bit quantization"
    echo "  base.en-q5_1 - Base English-only, 5-bit quantization"
    echo "  base-q8_0   - Base model, 8-bit quantization"
    echo ""
    echo "Small models (244 MB):"
    echo "  small       - Small model"
    echo "  small.en    - Small English-only model"
    echo "  small.en-tdrz - Small English-only with tinydiarize"
    echo "  small-q5_1  - Small model, 5-bit quantization"
    echo "  small.en-q5_1 - Small English-only, 5-bit quantization"
    echo "  small-q8_0  - Small model, 8-bit quantization"
    echo ""
    echo "Medium models (829 MB):"
    echo "  medium      - Medium model"
    echo "  medium.en   - Medium English-only model"
    echo "  medium-q5_0 - Medium model, 5-bit quantization"
    echo "  medium.en-q5_0 - Medium English-only, 5-bit quantization"
    echo "  medium-q8_0 - Medium model, 8-bit quantization"
    echo ""
    echo "Large models (1550 MB):"
    echo "  large-v1    - Large v1 model"
    echo "  large-v2    - Large v2 model"
    echo "  large-v2-q5_0 - Large v2 model, 5-bit quantization"
    echo "  large-v2-q8_0 - Large v2 model, 8-bit quantization"
    echo "  large-v3    - Large v3 model"
    echo "  large-v3-q5_0 - Large v3 model, 5-bit quantization"
    echo "  large-v3-turbo - Large v3 turbo model"
    echo "  large-v3-turbo-q5_0 - Large v3 turbo, 5-bit quantization"
    echo "  large-v3-turbo-q8_0 - Large v3 turbo, 8-bit quantization"
    echo ""
    echo "Notes:"
    echo "  .en = English-only models (smaller, faster, English-only)"
    echo "  -q5_1, -q8_0 = Quantized models (smaller file size, slightly less accurate)"
    echo "  -tdrz = Tinydiarize variant (supports speaker diarization)"
    echo ""
    echo "Example: $0 base"
}

# Check if model name is provided
if [ $# -ne 1 ]; then
    usage
    exit 1
fi

MODEL=$1

# Validate model name
if ! echo "$MODELS" | grep -q -w "$MODEL"; then
    echo "Error: Invalid model '$MODEL'"
    echo ""
    usage
    exit 1
fi

# Set download URL
BASE_URL="https://huggingface.co/ggerganov/whisper.cpp/resolve/main"
FILENAME="ggml-$MODEL.bin"
URL="$BASE_URL/$FILENAME"

# Change to the models directory
cd "$SCRIPT_DIR"

# Check if model already exists
if [ -f "$FILENAME" ]; then
    echo "Model '$MODEL' already exists at $SCRIPT_DIR/$FILENAME"
    exit 0
fi

echo "Downloading $MODEL model..."
echo "URL: $URL"
echo "Destination: $SCRIPT_DIR/$FILENAME"

# Download the model
if command -v curl >/dev/null 2>&1; then
    curl -L -o "$FILENAME" "$URL"
elif command -v wget >/dev/null 2>&1; then
    wget -O "$FILENAME" "$URL"
else
    echo "Error: Neither curl nor wget is available. Please install one of them."
    exit 1
fi

# Verify download
if [ $? -eq 0 ] && [ -f "$FILENAME" ]; then
    FILE_SIZE=$(stat -c%s "$FILENAME" 2>/dev/null || stat -f%z "$FILENAME" 2>/dev/null || echo "unknown")
    echo ""
    echo "Successfully downloaded $MODEL model"
    echo "File: $SCRIPT_DIR/$FILENAME"
    echo "Size: $FILE_SIZE bytes"
    echo ""
    echo "You can now use this model with whisper-spring applications."
else
    echo "Error: Failed to download the model"
    exit 1
fi