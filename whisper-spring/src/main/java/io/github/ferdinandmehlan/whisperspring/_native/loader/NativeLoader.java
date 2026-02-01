package io.github.ferdinandmehlan.whisperspring._native.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NativeLoader {

    private static final Logger log = LoggerFactory.getLogger(NativeLoader.class.getName());

    private static NativeLoader INSTANCE;

    public static synchronized NativeLoader getInstance() {
        if (INSTANCE == null) {
            log.info("Loading native libraries");
            try {
                log.debug("Attempting to load CUDA libraries first");
                CUDANativeLoader cudaLoader = new CUDANativeLoader();
                cudaLoader.loadLibraries();
                log.debug("CUDA libraries loaded successfully");
                INSTANCE = cudaLoader;
            } catch (UnsatisfiedLinkError | RuntimeException e) {
                log.debug("CUDA loading failed, falling back to CPU libraries: {}", e.getMessage());
                CPUNativeLoader cpuLoader = new CPUNativeLoader();
                cpuLoader.loadLibraries();
                log.debug("CPU libraries loaded successfully");
                INSTANCE = cpuLoader;
            }
        }
        return INSTANCE;
    }

    public static Path getWhisperLibPath() {
        return getInstance().loadedLibraries.get("whisper");
    }

    protected final Map<String, Path> loadedLibraries = new ConcurrentHashMap<>();

    protected abstract void loadLibraries();

    protected synchronized void loadLibrary(String libName, String engine) {
        log.debug("Loading library: name={}, engine={}", libName, engine);
        loadedLibraries.computeIfAbsent(libName, _ -> {
            Path libraryPath = extractLibrary(libName, engine);
            System.load(libraryPath.toString());
            return libraryPath;
        });
    }

    protected Path extractLibrary(String libName, String engine) {
        PlatformEnum platform = PlatformEnum.detect();
        String resourcePath = platform.getResourcePath(libName, engine);
        try (InputStream is = NativeLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new NativeLibraryNotFoundException(libName, engine, platform);
            }
            String fileName = Path.of(resourcePath).getFileName().toString();
            Path tempDir = Files.createTempDirectory("whisper-native-" + libName);
            Path target = tempDir.resolve(fileName);
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException e) {
            log.error("Error extracting native library from {}", resourcePath, e);
            throw new RuntimeException(e);
        }
    }
}
