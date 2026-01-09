package io.github.ferdinandmehlan.whisperspring.loader;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspring.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WhisperPropertiesTest extends BaseIntegrationTest {

    @Autowired
    private WhisperProperties whisperProperties;

    @Test
    public void testDefaultLibrariesMode() {
        // Default mode should be CPU
        assertThat(whisperProperties.getLibraries().getMode()).isEqualTo(WhisperProperties.Libraries.Mode.CPU);
    }

    @Test
    public void testDefaultLibrariesPath() {
        // Default path should be "./libraries"
        assertThat(whisperProperties.getLibraries().getPath()).isEqualTo("./libs");
    }
}
