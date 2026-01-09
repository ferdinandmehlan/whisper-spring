package io.github.ferdinandmehlan.whisperspringserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class WhisperSpringServerApplicationTest extends BaseIntegrationTest {

    @Test
    public void testContextLoads() {
        assertThat(testRestTemplate).isNotNull();
    }
}
