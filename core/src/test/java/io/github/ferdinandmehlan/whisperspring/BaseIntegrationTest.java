package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspring._native.WhisperNative;
import io.github.ferdinandmehlan.whisperspringtestcommon.BaseTest;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = BaseIntegrationTest.TestApplication.class)
public abstract class BaseIntegrationTest extends BaseTest {

    @Autowired
    protected WhisperProperties whisperProperties;

    /**
     * Creates a {@link WhisperNative} for the model and native library configured by the test profile.
     *
     * @return a new WhisperNative instance
     * @throws IOException if the model cannot be loaded
     */
    protected WhisperNative newWhisperNative() throws IOException {
        return new WhisperNative(whisperProperties.getModelPath(), whisperProperties.getNativeLibraryPath());
    }

    @SpringBootApplication
    public static class TestApplication {}
}
