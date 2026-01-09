package io.github.ferdinandmehlan.whisperspringserver.config;

import io.github.ferdinandmehlan.whisperspringserver.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

class OpenApiConfigTest extends BaseIntegrationTest {

    @Test
    void openApiSpec() {
        ResponseEntity<String> openApiSpecResponse =
                testRestTemplate.exchange("/v3/api-docs.yaml", HttpMethod.GET, null, String.class);
        assertWithYamlFile(openApiSpecResponse.getBody());
    }
}
