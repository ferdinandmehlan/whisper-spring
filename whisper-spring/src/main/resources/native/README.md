# Redistributables

The linux redistributables were built from [whisper.cpp](https://github.com/ggml-org/whisper.cpp.git) and tag v1.8.2.

## Linux Native Library

Built using:

```bash
cmake -B build -DBUILD_SHARED_LIBS=ON
cmake --build build -j --config Release
```

Alternatively with CUDA support:

```bash
cmake -B build -DGGML_CUDA=1 -DBUILD_SHARED_LIBS=ON
cmake --build build -j --config Release
```

The shared library will be located in:
- `whisper.cpp/build/ggml/src/libggml.so`
- `whisper.cpp/build/ggml/src/libggml-base.so`
- `whisper.cpp/build/ggml/src/libggml-cpu.so`
- `whisper.cpp/build/src/libwhisper.so`

See whisper.cpp for more build flags.
