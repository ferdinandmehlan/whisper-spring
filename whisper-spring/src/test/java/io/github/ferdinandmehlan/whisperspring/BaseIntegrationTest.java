package io.github.ferdinandmehlan.whisperspring;

import io.github.ferdinandmehlan.whisperspringtestcommon.BaseTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = BaseIntegrationTest.TestApplication.class)
public abstract class BaseIntegrationTest extends BaseTest {

    @SpringBootApplication
    public static class TestApplication {}
}
