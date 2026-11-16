package io.github.ferdinandmehlan.whisperspringserver.model;

import io.github.ferdinandmehlan.whisperspring.WhisperProperties;
import io.github.ferdinandmehlan.whisperspring.WhisperTranscriptionModel;
import io.github.ferdinandmehlan.whisperspringserver.model.api.ModelInfo;
import io.github.ferdinandmehlan.whisperspringserver.model.api.ModelListResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Service that knows which model the server runs with.
 *
 * <p>The model is fixed at startup: {@code whisper.model-path} names the file, and a
 * {@link WhisperTranscriptionModel} bean is created from it. There is no way to load or switch a
 * model at runtime, so the service reports what is there instead of holding a choice. That is
 * expected to change once several models can be served; the service is where it will.
 */
@Service
public class ModelService {

    private static final String MODEL_FILE_PREFIX = "ggml-";

    private final WhisperProperties whisperProperties;
    private final ObjectProvider<WhisperTranscriptionModel> transcriptionModel;

    /**
     * Creates a new ModelService.
     *
     * @param whisperProperties  the whisper configuration containing the model path
     * @param transcriptionModel the loaded model, absent when none is configured
     */
    public ModelService(
            WhisperProperties whisperProperties, ObjectProvider<WhisperTranscriptionModel> transcriptionModel) {
        this.whisperProperties = whisperProperties;
        this.transcriptionModel = transcriptionModel;
    }

    /**
     * Lists the models available to the server.
     *
     * <p>The model is reported as {@code active} only when it is actually loaded, which is the
     * case exactly when the {@link WhisperTranscriptionModel} bean exists. A configured but
     * unloadable model therefore shows up as an empty list rather than a broken entry.
     *
     * @return the loaded model, or an entry-less response when no model is loaded
     */
    public ModelListResponse list() {
        Optional<ModelInfo> active = activeModel();
        return new ModelListResponse(
                active.map(ModelInfo::id).orElse(null), active.stream().toList());
    }

    /**
     * Derives the info of the loaded model from its configured path.
     *
     * @return the loaded model, or empty when no model is loaded
     */
    public Optional<ModelInfo> activeModel() {
        if (transcriptionModel.getIfAvailable() == null) {
            return Optional.empty();
        }

        Path modelPath = Path.of(whisperProperties.getModelPath());
        Path fileName = modelPath.getFileName();
        String name = stripExtension(fileName.toString());
        String id = name.startsWith(MODEL_FILE_PREFIX) ? name.substring(MODEL_FILE_PREFIX.length()) : name;
        return Optional.of(new ModelInfo(id, name, size(modelPath)));
    }

    /**
     * Removes the file extension, so {@code ggml-tiny.bin} becomes {@code ggml-tiny}.
     *
     * @param fileName the model file name
     * @return the name without its extension
     */
    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    /**
     * Reads the model file size, tolerating a path the server can no longer read.
     *
     * @param modelPath the model file path
     * @return the size in bytes, or {@code null} if the file cannot be read
     */
    private static Long size(Path modelPath) {
        try {
            return Files.size(modelPath);
        } catch (IOException e) {
            return null;
        }
    }
}
