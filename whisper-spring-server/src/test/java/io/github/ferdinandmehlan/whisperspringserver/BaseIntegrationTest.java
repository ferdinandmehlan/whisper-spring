package io.github.ferdinandmehlan.whisperspringserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ferdinandmehlan.whisperspringtestcommon.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
@SpringBootTest(
        classes = WhisperSpringServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest extends BaseTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Override
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
