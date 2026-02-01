package io.github.ferdinandmehlan.whisperspring._native.loader;

public enum PlatformEnum {
    LINUX_X64("linux", "x64", ".so"),
    LINUX_AARCH64("linux", "aarch64", ".so"),
    MACOS_X64("macos", "x64", ".dylib"),
    MACOS_AARCH64("macos", "aarch64", ".dylib"),
    WINDOWS_X64("windows", "x64", ".dll"),
    WINDOWS_AARCH64("windows", "aarch64", ".dll"),
    UNSUPPORTED("unknown", "unknown", "");

    private final String osDir;
    private final String archDir;
    private final String extension;

    PlatformEnum(String osDir, String archDir, String extension) {
        this.osDir = osDir;
        this.archDir = archDir;
        this.extension = extension;
    }

    public static PlatformEnum detect() {
        return detect(System.getProperty("os.name"), System.getProperty("os.arch"));
    }

    static PlatformEnum detect(String os, String arch) {
        String normalizedOs =
                switch (os.toLowerCase()) {
                    case String s when s.contains("win") -> "windows";
                    case String s when s.contains("mac") -> "macos";
                    default -> "linux";
                };

        String normalizedArch =
                switch (arch.toLowerCase()) {
                    case "amd64", "x86_64", "x64" -> "x64";
                    case "aarch64", "arm64" -> "aarch64";
                    case "x86", "i386" -> "x86";
                    default -> throw new UnsupportedOperationException("Unsupported arch: " + arch);
                };

        for (PlatformEnum platform : values()) {
            if (platform.osDir.equals(normalizedOs) && platform.archDir.equals(normalizedArch)) {
                return platform;
            }
        }

        return UNSUPPORTED;
    }

    public String getOsDir() {
        return osDir;
    }

    public String getArchDir() {
        return archDir;
    }

    public String getExtension() {
        return extension;
    }

    public String getLibraryPrefix() {
        return osDir.equals("windows") ? "" : "lib";
    }

    public String getResourcePath(String libName, String engine) {
        String prefixedLibName = getLibraryPrefix() + libName;
        String prefixedEngine = engine != null ? "/" + engine : "";
        return "/native/" + osDir + "/" + archDir + prefixedEngine + "/" + prefixedLibName + extension;
    }
}
