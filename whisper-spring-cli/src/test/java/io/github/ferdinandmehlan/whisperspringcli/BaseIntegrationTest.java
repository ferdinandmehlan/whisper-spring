package io.github.ferdinandmehlan.whisperspringcli;

import io.github.ferdinandmehlan.whisperspring.WhisperService;
import io.github.ferdinandmehlan.whisperspringtestcommon.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest(classes = WhisperSpringCli.class)
public abstract class BaseIntegrationTest extends BaseTest {

    @Autowired
    WhisperService whisperService;

    @MockitoBean
    CommandLineRunner runner; // Mock runner bean so it does not autostart
}
