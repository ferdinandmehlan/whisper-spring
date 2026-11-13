package io.github.ferdinandmehlan.whisperspring._native.loader;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NativePlatformTest {

    @ParameterizedTest
    @CsvSource({
        "Linux, amd64, linux, x64, libwhisper.so",
        "Linux, x86_64, linux, x64, libwhisper.so",
        "Linux, aarch64, linux, aarch64, libwhisper.so",
        "Linux, arm64, linux, aarch64, libwhisper.so",
        "Linux, ppc64le, linux, ppc64, libwhisper.so",
        "Linux, riscv64, linux, riscv64, libwhisper.so",
        "Linux, s390x, linux, s390x, libwhisper.so",
        "Windows 10, amd64, windows, x64, whisper.dll",
        "Windows 11, aarch64, windows, aarch64, whisper.dll",
        "Mac OS X, x86_64, macos, x64, libwhisper.dylib",
        "Mac OS X, arm64, macos, aarch64, libwhisper.dylib",
        "FreeBSD, amd64, unix, x64, libwhisper.so"
    })
    void detectsPlatform(String os, String arch, String expectedOs, String expectedArch, String expectedLibraryName) {
        NativePlatform platform = NativePlatform.detect(os, arch);

        assertThat(platform.os().id()).isEqualTo(expectedOs);
        assertThat(platform.architecture().id()).isEqualTo(expectedArch);
        assertThat(platform.libraryFileName("whisper")).isEqualTo(expectedLibraryName);
        assertThat(platform.description()).isEqualTo(expectedOs + "/" + expectedArch);
    }

    @Test
    void supportsUnknownPlatformsWithoutFailing() {
        NativePlatform platform = NativePlatform.detect("Plan 9", "mips");

        assertThat(platform.os()).isEqualTo(NativePlatform.Os.UNKNOWN);
        assertThat(platform.architecture()).isEqualTo(NativePlatform.Architecture.UNKNOWN);
    }

    @Test
    void providesCommonWindowsLibraryNames() {
        NativePlatform platform = NativePlatform.detect("Windows 11", "amd64");

        assertThat(platform.libraryFileNames("whisper")).contains("whisper.dll", "libwhisper.dll");
    }

    @Test
    void providesVersionedLinuxLibraryNames() {
        NativePlatform platform = NativePlatform.detect("Linux", "amd64");

        assertThat(platform.libraryFileNames("whisper")).contains("libwhisper.so", "libwhisper.so.1");
    }
}
