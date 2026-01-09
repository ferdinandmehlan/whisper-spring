package io.github.ferdinandmehlan.whisperspring.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for extracting native libraries from JAR resources to a temporary directory.
 */
public class NativeExtractor {

    private static final Logger logger = LoggerFactory.getLogger(NativeExtractor.class);

    private final Path tempDir;

    public NativeExtractor() throws IOException {
        logger.debug("Creating temporary directory for native libraries");
        this.tempDir = Files.createTempDirectory("whisper-native");
        this.tempDir.toFile().deleteOnExit();
        logger.debug("Temporary directory created: {}", tempDir);
    }

    /**
     * Gets the temporary directory where native libraries are extracted.
     *
     * @return the temporary directory path
     */
    public Path getTempDir() {
        return tempDir;
    }

    /**
     * Extracts a native library from the JAR resources to a temporary directory.
     * All libraries are extracted to the same temporary directory.
     *
     * @param resourcePath the path to the resource within the JAR (e.g., "/native/linux-x86-64/libwhisper.so")
     * @return the path to the extracted library file
     * @throws IOException if extraction fails
     */
    public synchronized Path extractLibrary(String resourcePath) throws IOException {
        logger.debug("Attempting to extract library: {}", resourcePath);
        try (InputStream in = NativeExtractor.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                logger.error("Resource not found: {}", resourcePath);
                throw new IOException("Resource not found: " + resourcePath);
            }

            Path target = tempDir.resolve(Paths.get(resourcePath).getFileName());
            logger.debug("Extracting {} to {}", resourcePath, target);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            target.toFile().deleteOnExit();
            logger.debug("Successfully extracted library to: {}", target);
            return target;
        } catch (IOException e) {
            logger.error("Failed to extract library {}: {}", resourcePath, e.getMessage(), e);
            throw e;
        }
    }
}
