package io.github.ferdinandmehlan.whisperspring._native;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading native Whisper libraries.
 * Handles platform-specific library extraction and loading.
 */
public final class NativeLoader {

    private static final NativeLoader INSTANCE;
    private static final Logger log = LoggerFactory.getLogger(NativeLoader.class.getName());

    private static final Map<String, Path> loadedLibs = new ConcurrentHashMap<>();
    private Path tempDir;

    static {
        try {
            INSTANCE = new NativeLoader();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize NativeLoader", e);
        }
    }

    /**
     * Returns the singleton instance of NativeLoader.
     *
     * @return the NativeLoader instance
     */
    public static NativeLoader getInstance() {
        return INSTANCE;
    }

    private NativeLoader() {}

    private synchronized void loadLibraries() {
        log.info("Starting native library loading");
        try {
            this.tempDir = Files.createTempDirectory("whisper-native");
            log.info("Temp directory: {}", tempDir);
            boolean cudaAvailable = isCudaAvailable();
            log.info("CUDA available: {}", cudaAvailable);
            if (cudaAvailable) {
                log.info("Loading CUDA libraries");
                loadLibrary("ggml-base", "cuda");
                loadLibrary("ggml-cpu", "cuda");
                loadLibrary("ggml-cuda", "cuda");
                loadLibrary("ggml", "cuda");
                loadLibrary("whisper", "cuda");
            } else {
                log.info("Loading CPU libraries");
                loadLibrary("ggml-base");
                loadLibrary("ggml-cpu");
                loadLibrary("ggml");
                loadLibrary("whisper");
            }
            log.info("Native library loading complete");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isCudaAvailable() {
        log.info("Checking CUDA availability");
        String cudaPath = System.getenv("CUDA_PATH");
        String cudaHome = System.getenv("CUDA_HOME");
        log.info("CUDA_PATH: {}", cudaPath);
        log.info("CUDA_HOME: {}", cudaHome);

        if (cudaPath != null || cudaHome != null) {
            log.info("CUDA environment variables found, trying to load CUDA libraries");
            return tryLoadCudaLibraries();
        }

        log.info("No CUDA env vars, checking nvidia-smi");
        try {
            String nvidiaSmi = System.getProperty("os.name").toLowerCase().contains("win") ? "nvidia-smi.exe" : "nvidia-smi";
            log.info("Running: {}", nvidiaSmi);
            Process process = new ProcessBuilder(nvidiaSmi).start();
            int exitCode = process.waitFor();
            log.info("nvidia-smi exit code: {}", exitCode);
            if (exitCode == 0) {
                log.info("nvidia-smi succeeded, trying to load CUDA libraries");
                return tryLoadCudaLibraries();
            }
        } catch (Exception e) {
            log.info("nvidia-smi failed: {}", e.getMessage());
        }

        log.info("CUDA not available");
        return false;
    }

    private boolean tryLoadCudaLibraries() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] cudaLibraries;

        if (os.contains("win")) {
            cudaLibraries = new String[]{"nvcuda.dll", "cudart64_?.dll", "cublas64_?.dll"};
        } else if (os.contains("mac")) {
            cudaLibraries = new String[]{"libcuda.dylib", "libcudart.dylib", "libcublas.dylib"};
        } else {
            cudaLibraries = new String[]{"libcuda.so", "libcudart.so", "libcublas.so"};
        }

        log.info("Trying to load CUDA libraries: {}", java.util.Arrays.toString(cudaLibraries));
        for (String libPattern : cudaLibraries) {
            log.info("Attempting to load: {}", libPattern);
            if (!tryLoadSingleLibrary(libPattern)) {
                log.info("Failed to load: {}", libPattern);
                return false;
            }
            log.info("Successfully loaded: {}", libPattern);
        }

        log.info("All CUDA libraries loaded successfully");
        return true;
    }

    private boolean tryLoadSingleLibrary(String libPattern) {
        try {
            log.info("tryLoadSingleLibrary: {}", libPattern);
            if (libPattern.contains("?")) {
                String baseName = libPattern.replace("?", "");
                String[] searchPaths = getLibrarySearchPaths();
                log.info("Searching for wildcard pattern: {} in {} paths", baseName, searchPaths.length);

                for (String searchPath : searchPaths) {
                    log.info("Checking path: {}", searchPath);
                    java.io.File dir = new java.io.File(searchPath);
                    if (dir.exists() && dir.isDirectory()) {
                        java.io.File[] files = dir.listFiles((d, name) -> name.startsWith(baseName.replace(".dll", "").replace(".so", "").replace(".dylib", "")) &&
                                                                         (name.endsWith(".dll") || name.endsWith(".so") || name.endsWith(".dylib")));
                        if (files != null && files.length > 0) {
                            log.info("Found {} matching files, loading: {}", files.length, files[0].getAbsolutePath());
                            System.load(files[0].getAbsolutePath());
                            return true;
                        }
                    }
                }
                log.info("No files found for pattern: {}", libPattern);
                return false;
            } else {
                String libName = libPattern.replace(".dll", "").replace(".so", "").replace(".dylib", "").replace("lib", "");
                log.info("Direct load via System.loadLibrary: {}", libName);
                System.loadLibrary(libName);
                return true;
            }
        } catch (UnsatisfiedLinkError e) {
            log.info("Failed to load {}: {}", libPattern, e.getMessage());
            return false;
        } catch (Exception e) {
            log.info("Exception loading {}: {}", libPattern, e.getMessage());
            return false;
        }
    }

    private String[] getLibrarySearchPaths() {
        String os = System.getProperty("os.name").toLowerCase();
        java.util.List<String> paths = new java.util.ArrayList<>();

        if (os.contains("win")) {
            String path = System.getenv("PATH");
            if (path != null) {
                paths.addAll(java.util.Arrays.asList(path.split(";")));
            }
            String cudaPath = System.getenv("CUDA_PATH");
            if (cudaPath != null) {
                paths.add(cudaPath + "\\bin");
                paths.add(cudaPath + "\\lib\\x64");
            }
        } else {
            String ldLibraryPath = System.getenv("LD_LIBRARY_PATH");
            if (ldLibraryPath != null) {
                paths.addAll(java.util.Arrays.asList(ldLibraryPath.split(":")));
            }
            paths.add("/usr/local/cuda/lib64");
            paths.add("/usr/local/cuda/lib");
            paths.add("/usr/lib/x86_64-linux-gnu");
            paths.add("/usr/lib64");

            String cudaPath = System.getenv("CUDA_PATH");
            if (cudaPath != null) {
                paths.add(cudaPath + "/lib64");
                paths.add(cudaPath + "/lib");
            }
        }

        log.info("Library search paths: {}", paths);
        return paths.toArray(new String[0]);
    }

    private synchronized void loadLibrary(String libName) {
        loadLibrary(libName, null);
    }

    private synchronized void loadLibrary(String libName, String engine) {
        log.info("Loading library: name={}, engine={}", libName, engine);
        loadedLibs.computeIfAbsent(libName, name -> {
            String resourcePath = getResourcePath(name, engine);
            log.info("Resource path: {}", resourcePath);
            Path libraryPath = extractLibrary(resourcePath);
            log.info("Loading extracted library: {}", libraryPath);
            System.load(libraryPath.toString());
            log.info("Successfully loaded: {}", libraryPath);
            return libraryPath;
        });
    }

    private String getResourcePath(String libName, String engine) {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        String osDir =
                switch (os) {
                    case String s when s.contains("win") -> "windows";
                    case String s when s.contains("mac") -> "macos";
                    default -> "linux";
                };

        String archDir =
                switch (arch) {
                    case "amd64", "x86_64", "x64" -> "x64";
                    case "aarch64", "arm64" -> "aarch64";
                    case "x86", "i386" -> "x86";
                    default -> throw new UnsupportedOperationException("Unsupported arch: " + arch);
                };

        String extension =
                switch (osDir) {
                    case "windows" -> ".dll";
                    case "macos" -> ".dylib";
                    default -> ".so";
                };

        String prefixedLibName = addLibPrefix(libName, osDir);
        String prefixedEngine = engine != null ? "/" + engine : "";
        return "/native/" + osDir + "/" + archDir + prefixedEngine + "/" + prefixedLibName + extension;
    }

    private String addLibPrefix(String libName, String osDir) {
        if (!osDir.equals("windows")) {
            return "lib" + libName;
        }
        return libName;
    }

    private Path extractLibrary(String resourcePath) {
        log.info("Extracting library from resource: {}", resourcePath);
        try (InputStream is = NativeLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            String fileName = Path.of(resourcePath).getFileName().toString();
            Path target = tempDir.resolve(fileName);
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException e) {
            log.error("Error extracting native library from {}", resourcePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the path to the loaded Whisper library.
     *
     * @return the path to the Whisper library file
     */
    public static Path getWhisperLibPath() {
        Path whisperLib = getInstance().loadedLibs.get("whisper");
        if (whisperLib != null) {
            return whisperLib;
        }
        getInstance().loadLibraries();
        return getInstance().loadedLibs.get("whisper");
    }
}
