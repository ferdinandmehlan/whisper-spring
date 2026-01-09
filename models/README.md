# Models

Whisper.cpp works with GGUF-type Whisper models.
For tests a tiny model is automatically downloaded by gradle tasks.
You can also download any compatible GGUF Whisper model to use.

## Download Models

Use the provided download script to easily download models:

```sh
./download-model.sh base
```

This will download the base model to the current directory.

### Available Models

#### Tiny Models (39 MB)
- `tiny` - Tiny model
- `tiny.en` - Tiny English-only model
- `tiny-q5_1` - Tiny model, 5-bit quantization
- `tiny.en-q5_1` - Tiny English-only, 5-bit quantization
- `tiny-q8_0` - Tiny model, 8-bit quantization

#### Base Models (74 MB)
- `base` - Base model
- `base.en` - Base English-only model
- `base-q5_1` - Base model, 5-bit quantization
- `base.en-q5_1` - Base English-only, 5-bit quantization
- `base-q8_0` - Base model, 8-bit quantization

#### Small Models (244 MB)
- `small` - Small model
- `small.en` - Small English-only model
- `small.en-tdrz` - Small English-only with tinydiarize
- `small-q5_1` - Small model, 5-bit quantization
- `small.en-q5_1` - Small English-only, 5-bit quantization
- `small-q8_0` - Small model, 8-bit quantization

#### Medium Models (829 MB)
- `medium` - Medium model
- `medium.en` - Medium English-only model
- `medium-q5_0` - Medium model, 5-bit quantization
- `medium.en-q5_0` - Medium English-only, 5-bit quantization
- `medium-q8_0` - Medium model, 8-bit quantization

#### Large Models (1550 MB)
- `large-v1` - Large v1 model
- `large-v2` - Large v2 model
- `large-v2-q5_0` - Large v2 model, 5-bit quantization
- `large-v2-q8_0` - Large v2 model, 8-bit quantization
- `large-v3` - Large v3 model
- `large-v3-q5_0` - Large v3 model, 5-bit quantization
- `large-v3-turbo` - Large v3 turbo model
- `large-v3-turbo-q5_0` - Large v3 turbo, 5-bit quantization
- `large-v3-turbo-q8_0` - Large v3 turbo, 8-bit quantization

**Notes:**
- `.en` = English-only models (smaller, faster, English-only)
- `-q5_1`, `-q8_0` = Quantized models (smaller file size, slightly less accurate)
- `-tdrz` = Tinydiarize variant (supports speaker diarization)

Run `./download-model.sh` without arguments to see all available models and usage.

Alternatively, download manually from [Hugging Face](https://huggingface.co/ggerganov/whisper.cpp).
