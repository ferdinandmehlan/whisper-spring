package io.github.ferdinandmehlan.whisperspringserver.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ferdinandmehlan.whisperspringserver.BaseIntegrationTest;
import io.github.ferdinandmehlan.whisperspringserver.model.api.ModelInfo;
import io.github.ferdinandmehlan.whisperspringserver.model.api.ModelListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ModelControllerTest extends BaseIntegrationTest {

    @Test
    public void testListsTheModelLoadedAtStartup() {
        ResponseEntity<ModelListResponse> response = models();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        ModelListResponse body = response.getBody();
        assertThat(body.active()).isEqualTo("tiny");
        assertThat(body.models()).hasSize(1);

        ModelInfo model = body.models().getFirst();
        assertThat(model.id()).isEqualTo("tiny");
        assertThat(model.name()).isEqualTo("ggml-tiny");
        assertThat(model.sizeBytes()).isPositive();
    }

    private ResponseEntity<ModelListResponse> models() {
        return testRestTemplate.getForEntity("/api/models", ModelListResponse.class);
    }
}
