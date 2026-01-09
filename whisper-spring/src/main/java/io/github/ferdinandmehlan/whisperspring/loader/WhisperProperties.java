package io.github.ferdinandmehlan.whisperspring.loader;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Whisper Spring integration.
 */
@ConfigurationProperties(prefix = "whisper")
public class WhisperProperties {

    /**
     * Library configuration for loading native libraries.
     */
    private Libraries libraries = new Libraries();

    public Libraries getLibraries() {
        return libraries;
    }

    public void setLibraries(Libraries libraries) {
        this.libraries = libraries;
    }

    public static class Libraries {

        /**
         * Mode for loading libraries: CPU, CUDA, or Custom.
         * Default is CPU.
         */
        private Mode mode = Mode.CPU;

        /**
         * Path to load custom libraries from if mode is Custom.
         * Default is "./libraries".
         */
        private String path = "./libraries";

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public enum Mode {
            CPU,
            CUDA,
            Custom
        }
    }
}
