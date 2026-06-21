# Redistributables

The linux redistributables were built from [whisper.cpp](https://github.com/ggml-org/whisper.cpp.git) and tag v1.8.2.

## CPU Only

Built using:

```bash
cmake -B build -DBUILD_SHARED_LIBS=ON
cmake --build build -j --config Release
```

## CUDA

The provided binaries are built with CUDA 12.9 and gpu architecture targets:

61: Pascal (Tesla P40, GTX 1080)
70 : Volta (Tesla V100)
75: Turing (GTX 16xx series, RTX 2000 series)
80: Ampere (A30, A100)
86: Ampere (A40, RTX 3000 series)
89: Ada Lovelace (L4, L40, RTX 4000 series)
90 : Hopper (H100, H200)

```bash
cmake -B build -DGGML_CUDA=ON \
  -DBUILD_SHARED_LIBS=ON \
  -DCMAKE_CUDA_ARCHITECTURES="61;70;75;80;86;89;90" \
  -DCMAKE_CUDA_FLAGS="-t 0"
cmake --build build -j --config Release
```

The shared library will be located in:
- `whisper.cpp/build/ggml/src/libggml.so`
- `whisper.cpp/build/ggml/src/libggml-base.so`
- `whisper.cpp/build/ggml/src/libggml-cpu.so`
- `whisper.cpp/build/src/libwhisper.so`

See whisper.cpp for more build flags.
