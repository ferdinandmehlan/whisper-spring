package io.github.ferdinandmehlan.whisperspringserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Provides the OpenAPI specification for the Whisper Spring REST API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI specification bean for API documentation.
     *
     * @return the configured OpenAPI specification
     */
    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
                .info(new Info().title("Whisper Spring REST API").version("1.0"))
                .servers(List.of(new Server().url("http://localhost:8080")));
    }
}
