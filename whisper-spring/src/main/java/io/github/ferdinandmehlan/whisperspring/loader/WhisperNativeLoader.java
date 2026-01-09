package io.github.ferdinandmehlan.whisperspring.loader;

import com.sun.jna.NativeLibrary;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spring component that extracts native libraries from the JAR and registers them with JNA
 * on application startup. This ensures that Whisper can load its native dependencies
 * regardless of the deployment environment.
 */
@Component
public class WhisperNativeLoader {

    private static final Logger logger = LoggerFactory.getLogger(WhisperNativeLoader.class);

    @Autowired
    private NativeExtractor nativeExtractor;

    @Autowired
    private WhisperProperties whisperProperties;

    /**
     * Initializes the native library loader by loading the appropriate libraries
     * based on the configuration (CPU, CUDA, or Custom).
     *
     * @throws IOException if library loading fails
     */
    @PostConstruct
    public void init() throws IOException {
        switch (whisperProperties.getLibraries().getMode()) {
            case CPU:
                loadNativeLibrariesCpu();
                break;
            case CUDA:
                loadNativeLibrariesCuda();
                break;
            case Custom:
                loadNativeLibrariesCustom();
                break;
        }
    }

    /**
     * Loads the CPU-optimized native libraries required for Whisper functionality.
     * This includes the base GGML library, CPU-specific optimizations, and the main Whisper library.
     *
     * @throws IOException if any library fails to load
     */
    public void loadNativeLibrariesCpu() throws IOException {
        // Preload libraries
        loadNativeLibrary("/native/linux-x86-64/libggml-base.so");
        loadNativeLibrary("/native/linux-x86-64/libggml-cpu.so");
        loadNativeLibrary("/native/linux-x86-64/libggml.so");
        loadNativeLibrary("/native/linux-x86-64/libwhisper.so");

        // Also register the extraction directory with JNA for library loading as fallback
        addJNALibraryPath(nativeExtractor.getTempDir());
        logger.info("Native libraries loaded successfully");
    }

    /**
     * Loads the CUDA-accelerated native libraries required for Whisper functionality.
     * This includes the base GGML library, CPU fallbacks, CUDA optimizations, and the main Whisper library.
     * Requires compatible NVIDIA GPU hardware and drivers.
     *
     * @throws IOException if any library fails to load
     */
    public void loadNativeLibrariesCuda() throws IOException {
        // Preload libraries
        loadNativeLibrary("/native/linux-x86-64/cuda/libggml-base.so");
        loadNativeLibrary("/native/linux-x86-64/cuda/libggml-cpu.so");
        loadNativeLibrary("/native/linux-x86-64/cuda/libggml-cuda.so.0");
        loadNativeLibrary("/native/linux-x86-64/cuda/libggml.so");
        loadNativeLibrary("/native/linux-x86-64/cuda/libwhisper.so");

        // Also register the extraction directory with JNA for library loading as fallback
        addJNALibraryPath(nativeExtractor.getTempDir());
        logger.info("Native cuda libraries loaded successfully");
    }

    /**
     * Loads custom native libraries from the specified path.
     * This allows users to provide their own compiled libraries.
     */
    public void loadNativeLibrariesCustom() {
        // Register the custom directory with JNA
        String customPath = whisperProperties.getLibraries().getPath();
        addJNALibraryPath(Path.of(customPath));
        logger.info("Custom native libraries loaded successfully from {}", customPath);
    }

    /**
     * Registers the native library directory with JNA (Java Native Access) to enable
     * dynamic loading of native libraries. This sets the library search path and
     * adds specific search paths for GGML and Whisper libraries.
     *
     * @param nativeDir the directory containing the extracted native libraries
     */
    private void addJNALibraryPath(Path nativeDir) {
        String nativeDirString = nativeDir.toString();
        System.setProperty("jna.library.path", nativeDirString);
        NativeLibrary.addSearchPath("ggml", nativeDirString);
        NativeLibrary.addSearchPath("whisper", nativeDirString);
    }

    /**
     * Loads a single native library from the JAR resources.
     * The library is first extracted to a temporary directory, then loaded into the JVM.
     *
     * @param library the resource path to the native library within the JAR (e.g., "/native/linux-x86-64/libwhisper.so")
     * @throws IOException if the library cannot be extracted or loaded
     */
    public void loadNativeLibrary(String library) throws IOException {
        logger.info("Loading {}", library);
        Path path = nativeExtractor.extractLibrary(library);
        System.load(path.toString());
    }

    /**
     * Loads a single native library from a file path.
     *
     * @param libraryPath the path to the native library file
     * @throws IOException if the library cannot be loaded
     */
    public void loadNativeLibraryFromPath(Path libraryPath) throws IOException {
        logger.info("Loading {}", libraryPath);
        if (!libraryPath.toFile().exists()) {
            throw new IOException("Library file does not exist: " + libraryPath);
        }
        System.load(libraryPath.toString());
    }
}
