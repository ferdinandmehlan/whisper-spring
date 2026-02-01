package io.github.ferdinandmehlan.whisperspring._native.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ferdinandmehlan.whisperspring.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PlatformEnumTest extends BaseIntegrationTest {

    @ParameterizedTest
    @CsvSource({
        "Linux, amd64, linux, x64, .so, lib, /native/linux/x64/libwhisper.so, /native/linux/x64/cuda/libwhisper.so",
        "Linux, aarch64, linux, aarch64, .so, lib, /native/linux/aarch64/libwhisper.so, /native/linux/aarch64/cuda/libwhisper.so",
        "Windows 10, amd64, windows, x64, .dll, '', /native/windows/x64/whisper.dll, /native/windows/x64/cuda/whisper.dll",
        "Mac OS X, x86_64, macos, x64, .dylib, lib, /native/macos/x64/libwhisper.dylib, /native/macos/x64/cuda/libwhisper.dylib",
        "Mac OS X, arm64, macos, aarch64, .dylib, lib, /native/macos/aarch64/libwhisper.dylib, /native/macos/aarch64/cuda/libwhisper.dylib"
    })
    void testPlatformEnum(
            String os,
            String arch,
            String expectedOs,
            String expectedArch,
            String expectedExt,
            String expectedPrefix,
            String expectedCpuPath,
            String expectedCudaPath) {

        PlatformEnum platform = PlatformEnum.detect(os, arch);

        assertThat(platform.getOsDir()).isEqualTo(expectedOs);
        assertThat(platform.getArchDir()).isEqualTo(expectedArch);
        assertThat(platform.getExtension()).isEqualTo(expectedExt);
        assertThat(platform.getLibraryPrefix()).isEqualTo(expectedPrefix);
        assertThat(platform.getResourcePath("whisper", null)).isEqualTo(expectedCpuPath);
        assertThat(platform.getResourcePath("whisper", "cuda")).isEqualTo(expectedCudaPath);
    }

    @ParameterizedTest
    @CsvSource({"Linux, unsupported, Unsupported arch: unsupported", "Windows, arm, Unsupported arch: arm"})
    void testDetectUnsupportedArch(String os, String arch, String expectedMessage) {
        assertThatThrownBy(() -> PlatformEnum.detect(os, arch))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void testNativeLibraryNotFoundExceptionMessage() {
        PlatformEnum platform = PlatformEnum.detect("win", "x64");
        NativeLibraryNotFoundException ex = new NativeLibraryNotFoundException("whisper", "cuda", platform);
        assertWithFile(ex.getMessage());
    }
}
