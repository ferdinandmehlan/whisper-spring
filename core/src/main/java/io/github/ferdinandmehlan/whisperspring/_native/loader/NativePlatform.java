package io.github.ferdinandmehlan.whisperspring._native.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Detected operating system and architecture used to locate native libraries.
 *
 * <p>Platform detection is intentionally lenient: an unknown platform is not an error when the user
 * supplies an explicit library file. It only affects the default library file names when a directory
 * is configured.
 */
public final class NativePlatform {

    private final Os os;
    private final Architecture architecture;

    private NativePlatform(Os os, Architecture architecture) {
        this.os = os;
        this.architecture = architecture;
    }

    /**
     * Detects the current platform.
     *
     * @return the detected platform
     */
    public static NativePlatform detect() {
        return detect(System.getProperty("os.name", ""), System.getProperty("os.arch", ""));
    }

    static NativePlatform detect(String osName, String archName) {
        return new NativePlatform(detectOs(osName), detectArchitecture(archName));
    }

    private static Os detectOs(String osName) {
        String normalized = osName.toLowerCase(Locale.ROOT);
        if (normalized.contains("win")) {
            return Os.WINDOWS;
        }
        if (normalized.contains("mac") || normalized.contains("darwin")) {
            return Os.MACOS;
        }
        if (normalized.contains("linux")) {
            return Os.LINUX;
        }
        if (normalized.contains("bsd")
                || normalized.contains("unix")
                || normalized.contains("solaris")
                || normalized.contains("aix")) {
            return Os.UNIX;
        }
        return Os.UNKNOWN;
    }

    private static Architecture detectArchitecture(String archName) {
        String normalized = archName.toLowerCase(Locale.ROOT);
        if (normalized.equals("amd64") || normalized.equals("x86_64") || normalized.equals("x64")) {
            return Architecture.X64;
        }
        if (normalized.equals("aarch64") || normalized.equals("arm64")) {
            return Architecture.AARCH64;
        }
        if (normalized.equals("x86") || normalized.matches("i[3-6]86")) {
            return Architecture.X86;
        }
        if (normalized.startsWith("arm")) {
            return Architecture.ARM;
        }
        if (normalized.startsWith("ppc64")) {
            return Architecture.PPC64;
        }
        if (normalized.startsWith("riscv")) {
            return Architecture.RISCV64;
        }
        if (normalized.startsWith("s390")) {
            return Architecture.S390X;
        }
        return Architecture.UNKNOWN;
    }

    /**
     * Returns the platform-specific file name for a native library.
     *
     * @param baseName the library base name, for example {@code whisper}
     * @return the file name, for example {@code libwhisper.so}
     */
    public String libraryFileName(String baseName) {
        return os.libraryPrefix + baseName + os.libraryExtension;
    }

    /**
     * Finds a native library in a directory. The platform-specific name is searched first, followed by
     * common aliases such as {@code libwhisper.dll} for MinGW builds and versioned Linux libraries.
     *
     * @param directory the directory to search
     * @param baseName the library base name, for example {@code whisper}
     * @return the library path, or {@code null} if no matching library was found
     */
    public Path findLibrary(Path directory, String baseName) {
        for (String candidate : libraryFileNames(baseName)) {
            Path path = directory.resolve(candidate);
            if (Files.isRegularFile(path)) {
                return path;
            }
        }

        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(Files::isRegularFile)
                    .filter(path -> matchesLibrary(path, baseName))
                    .sorted()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the candidate file names for a native library.
     *
     * @param baseName the library base name
     * @return candidate file names, most specific first
     */
    public List<String> libraryFileNames(String baseName) {
        List<String> names = new ArrayList<>();
        switch (os) {
            case LINUX, UNIX -> {
                names.add("lib" + baseName + ".so");
                names.add("lib" + baseName + ".so.1");
                names.add("lib" + baseName + ".so.0");
            }
            case MACOS -> names.add("lib" + baseName + ".dylib");
            case WINDOWS -> {
                names.add(baseName + ".dll");
                names.add("lib" + baseName + ".dll");
            }
            case UNKNOWN -> {
                names.add(libraryFileName(baseName));
                names.add("lib" + baseName + ".so");
                names.add("lib" + baseName + ".dylib");
                names.add(baseName + ".dll");
            }
        }
        return names;
    }

    private boolean matchesLibrary(Path path, String baseName) {
        String fileName = path.getFileName().toString();
        for (String candidate : libraryFileNames(baseName)) {
            if (fileName.equalsIgnoreCase(candidate)) {
                return true;
            }
        }
        return switch (os) {
            case LINUX, UNIX -> fileName.startsWith("lib" + baseName + ".so.");
            case MACOS -> fileName.startsWith("lib" + baseName + ".dylib.");
            case WINDOWS ->
                fileName.equalsIgnoreCase(baseName + ".dll") || fileName.equalsIgnoreCase("lib" + baseName + ".dll");
            case UNKNOWN ->
                fileName.contains(baseName)
                        && (fileName.endsWith(".so")
                                || fileName.contains(".so.")
                                || fileName.endsWith(".dylib")
                                || fileName.endsWith(".dll"));
        };
    }

    /**
     * Returns the operating system family.
     *
     * @return the operating system family
     */
    public Os os() {
        return os;
    }

    /**
     * Returns the CPU architecture.
     *
     * @return the CPU architecture
     */
    public Architecture architecture() {
        return architecture;
    }

    /**
     * Returns a human-readable platform description, for example {@code linux/x64}.
     *
     * @return the platform description
     */
    public String description() {
        return os.id + "/" + architecture.id;
    }

    @Override
    public String toString() {
        return description();
    }

    /**
     * Supported operating system families.
     */
    public enum Os {
        LINUX("linux", ".so", "lib"),
        MACOS("macos", ".dylib", "lib"),
        WINDOWS("windows", ".dll", ""),
        UNIX("unix", ".so", "lib"),
        UNKNOWN("unknown", "", "lib");

        private final String id;
        private final String libraryExtension;
        private final String libraryPrefix;

        Os(String id, String libraryExtension, String libraryPrefix) {
            this.id = id;
            this.libraryExtension = libraryExtension;
            this.libraryPrefix = libraryPrefix;
        }

        /**
         * Returns the stable operating system identifier.
         *
         * @return the identifier
         */
        public String id() {
            return id;
        }

        /**
         * Returns the shared library extension.
         *
         * @return the extension including the leading dot
         */
        public String libraryExtension() {
            return libraryExtension;
        }

        /**
         * Returns the shared library file name prefix.
         *
         * @return the prefix, empty on Windows
         */
        public String libraryPrefix() {
            return libraryPrefix;
        }
    }

    /**
     * Supported CPU architecture families.
     */
    public enum Architecture {
        X64("x64"),
        AARCH64("aarch64"),
        X86("x86"),
        ARM("arm"),
        PPC64("ppc64"),
        RISCV64("riscv64"),
        S390X("s390x"),
        UNKNOWN("unknown");

        private final String id;

        Architecture(String id) {
            this.id = id;
        }

        /**
         * Returns the stable architecture identifier.
         *
         * @return the identifier
         */
        public String id() {
            return id;
        }
    }
}
