package io.github.ferdinandmehlan.whisperspring._native.loader;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A loaded whisper native library and its symbol lookup.
 *
 * <p>The library is loaded from the filesystem. There is no classpath extraction and no automatic
 * CPU/CUDA selection: the caller provides a file or a directory, and the libraries found there are
 * used as-is. CUDA users must provide the CUDA runtime themselves.
 *
 * <p>The returned instance owns the {@link Arena} used for the native library handle and must be
 * closed when the application shuts down.
 */
public final class NativeLibrary implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(NativeLibrary.class);

    private static final String LIBRARY_PATH_PROPERTY = "whisper.native-library-path";
    private static final String LIBRARY_PATH_ENV = "WHISPER_NATIVE_LIBRARY_PATH";

    private static final List<String> DEPENDENCY_ORDER = List.of(
            "ggml-base",
            "ggml-cpu",
            "ggml-cuda",
            "ggml-vulkan",
            "ggml-metal",
            "ggml-opencl",
            "ggml-sycl",
            "ggml-blas",
            "ggml",
            "ggml-rpc");

    private final Path path;
    private final List<Path> dependencies;
    private final Arena arena;
    private final SymbolLookup lookup;
    private final String version;

    private NativeLibrary(Path path, List<Path> dependencies, Arena arena, SymbolLookup lookup, String version) {
        this.path = path;
        this.dependencies = List.copyOf(dependencies);
        this.arena = arena;
        this.lookup = lookup;
        this.version = version;
    }

    /**
     * Loads the native library configured by the {@code whisper.native-library-path} system property or
     * the {@code WHISPER_NATIVE_LIBRARY_PATH} environment variable.
     *
     * @return the loaded native library
     */
    public static NativeLibrary loadDefault() {
        String configuredPath = System.getProperty(LIBRARY_PATH_PROPERTY);
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv(LIBRARY_PATH_ENV);
        }
        if (configuredPath == null || configuredPath.isBlank()) {
            throw new NativeLibraryNotFoundException("""
                    Native library path is not configured.

                    Set whisper.native-library-path to the whisper native library file or its directory.
                    For programmatic use, pass the path directly to the WhisperNative constructor.
                    """);
        }
        return load(Path.of(configuredPath));
    }

    /**
     * Loads a native library from a file or directory.
     *
     * @param path the library file, or a directory containing the platform-specific whisper library
     * @return the loaded native library
     */
    public static NativeLibrary load(String path) {
        if (path == null || path.isBlank()) {
            throw new NativeLibraryNotFoundException("Native library path must not be blank.");
        }
        return load(Path.of(path));
    }

    /**
     * Loads a native library from a file or directory.
     *
     * <p>If a directory is given, the platform-specific whisper library is located first. Known ggml
     * dependencies in the same directory are preloaded in dependency order so that libraries built
     * without a usable {@code $ORIGIN} rpath still work.
     *
     * @param configuredPath the library file, or a directory containing the native libraries
     * @return the loaded native library
     */
    public static NativeLibrary load(Path configuredPath) {
        if (configuredPath == null) {
            throw new NativeLibraryNotFoundException("Native library path must not be null.");
        }

        NativePlatform platform = NativePlatform.detect();
        Path path = configuredPath.toAbsolutePath().normalize();
        Path whisperLibrary = resolveWhisperLibrary(path, platform);
        List<Path> dependencies = preloadDependencies(whisperLibrary, platform);

        Arena arena = Arena.ofShared();
        try {
            SymbolLookup lookup = SymbolLookup.libraryLookup(whisperLibrary, arena);
            String version = readVersion(lookup);
            log.info("Loaded whisper native library {} (version {}, platform {})", whisperLibrary, version, platform);
            return new NativeLibrary(whisperLibrary, dependencies, arena, lookup, version);
        } catch (RuntimeException | Error e) {
            arena.close();
            throw new IllegalStateException("""
                    Failed to load whisper native library: %s
                    Platform: %s
                    Make sure libwhisper and all ggml dependencies are in the same directory and match the platform.
                    If this is a CUDA build, the CUDA runtime must be available on the library path.
                    """.formatted(whisperLibrary, platform), e);
        }
    }

    private static Path resolveWhisperLibrary(Path path, NativePlatform platform) {
        if (Files.isRegularFile(path)) {
            return path;
        }
        if (Files.isDirectory(path)) {
            Path library = platform.findLibrary(path, "whisper");
            if (library != null) {
                return library;
            }
        }
        throw new NativeLibraryNotFoundException(path, platform);
    }

    private static List<Path> preloadDependencies(Path whisperLibrary, NativePlatform platform) {
        Path directory = whisperLibrary.getParent();
        if (directory == null || !Files.isDirectory(directory)) {
            return List.of();
        }

        List<Path> loaded = new ArrayList<>();
        for (String dependencyName : DEPENDENCY_ORDER) {
            Path dependency = platform.findLibrary(directory, dependencyName);
            if (dependency == null || dependency.equals(whisperLibrary)) {
                continue;
            }
            try {
                System.load(dependency.toString());
                loaded.add(dependency);
                log.debug("Preloaded native dependency {}", dependency);
            } catch (UnsatisfiedLinkError e) {
                throw new IllegalStateException("""
                        Failed to load native dependency: %s
                        whisper library: %s
                        Platform: %s
                        The dependency must be compatible with this platform. For CUDA builds, install or provide the CUDA runtime.
                        """.formatted(dependency, whisperLibrary, platform), e);
            }
        }
        return loaded;
    }

    private static String readVersion(SymbolLookup lookup) {
        try {
            Linker linker = Linker.nativeLinker();
            MethodHandle version = linker.downcallHandle(
                    lookup.find("whisper_version").orElseThrow(), FunctionDescriptor.of(ValueLayout.ADDRESS));
            MemorySegment versionString = (MemorySegment) version.invoke();
            if (versionString.equals(MemorySegment.NULL)) {
                return "unknown";
            }
            return versionString.reinterpret(Long.MAX_VALUE).getString(0);
        } catch (Throwable t) {
            log.warn("Could not determine whisper native library version", t);
            return "unknown";
        }
    }

    /**
     * Returns the path of the loaded whisper library.
     *
     * @return the library path
     */
    public Path path() {
        return path;
    }

    /**
     * Returns the dependencies that were preloaded before the whisper library.
     *
     * @return the preloaded dependency paths
     */
    public List<Path> dependencies() {
        return dependencies;
    }

    /**
     * Returns the symbol lookup for the loaded library.
     *
     * @return the symbol lookup
     */
    public SymbolLookup lookup() {
        return lookup;
    }

    /**
     * Returns the version reported by {@code whisper_version()}, or {@code unknown} if it could not be
     * read.
     *
     * @return the native library version
     */
    public String version() {
        return version;
    }

    @Override
    public void close() {
        arena.close();
    }

    @Override
    public String toString() {
        return "NativeLibrary{" + path + ", version=" + version + '}';
    }
}
