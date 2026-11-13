package io.github.ferdinandmehlan.whisperspring._native.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NativeLibraryTest {

    private static final NativePlatform PLATFORM = NativePlatform.detect();

    private static final Path NATIVE_LIBRARY_DIRECTORY = Path.of("../build/native/lib");

    @BeforeEach
    void requireNativeLibrariesForThisPlatform() {
        Assumptions.assumeTrue(
                Files.isDirectory(NATIVE_LIBRARY_DIRECTORY),
                () -> "No test native libraries for platform " + PLATFORM.description());
    }

    @Test
    void loadsNativeLibraryFromDirectory() {
        try (NativeLibrary nativeLibrary = NativeLibrary.load(NATIVE_LIBRARY_DIRECTORY)) {
            assertThat(nativeLibrary.path()).isRegularFile();
            assertThat(nativeLibrary.path().getFileName().toString()).contains("whisper");
            assertThat(nativeLibrary.version()).isNotBlank().isNotEqualTo("unknown");
            assertThat(nativeLibrary.dependencies()).isNotEmpty();
        }
    }

    @Test
    void loadsNativeLibraryFromFile() {
        Path whisperLibrary = PLATFORM.findLibrary(NATIVE_LIBRARY_DIRECTORY, "whisper");
        assertThat(whisperLibrary).isNotNull();

        try (NativeLibrary nativeLibrary = NativeLibrary.load(whisperLibrary)) {
            assertThat(nativeLibrary.path())
                    .isEqualTo(whisperLibrary.toAbsolutePath().normalize());
            assertThat(nativeLibrary.version()).isNotBlank().isNotEqualTo("unknown");
        }
    }

    @Test
    void loadsSameLibraryRepeatedly() {
        try (NativeLibrary first = NativeLibrary.load(NATIVE_LIBRARY_DIRECTORY);
                NativeLibrary second = NativeLibrary.load(NATIVE_LIBRARY_DIRECTORY)) {
            assertThat(first.path()).isEqualTo(second.path());
            assertThat(first.lookup().find("whisper_version")).isPresent();
            assertThat(second.lookup().find("whisper_version")).isPresent();
        }
    }

    @Test
    void failsWithHelpfulMessageWhenPathDoesNotExist() {
        Path missing = Path.of("../build/native/lib/does-not-exist");
        assertThatThrownBy(() -> NativeLibrary.load(missing))
                .isInstanceOf(NativeLibraryNotFoundException.class)
                .hasMessageContaining("Native library not found")
                .hasMessageContaining("whisper.native-library-path")
                .hasMessageContaining(PLATFORM.libraryFileName("whisper"));
    }

    @Test
    void failsWithHelpfulMessageForEmptyDirectory() throws Exception {
        Path emptyDirectory = Files.createTempDirectory("whisper-native-empty");
        try {
            assertThatThrownBy(() -> NativeLibrary.load(emptyDirectory))
                    .isInstanceOf(NativeLibraryNotFoundException.class)
                    .hasMessageContaining("Native library not found");
        } finally {
            Files.deleteIfExists(emptyDirectory);
        }
    }

    @Test
    void rejectsBlankPath() {
        assertThatThrownBy(() -> NativeLibrary.load(" "))
                .isInstanceOf(NativeLibraryNotFoundException.class)
                .hasMessageContaining("must not be blank");
    }
}
