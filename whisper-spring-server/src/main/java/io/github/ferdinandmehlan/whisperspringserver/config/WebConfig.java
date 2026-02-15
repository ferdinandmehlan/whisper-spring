package io.github.ferdinandmehlan.whisperspringserver.config;

import io.github.ferdinandmehlan.whisperspringserver.transcription.ResponseFormat;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web configuration for the Whisper Spring server.
 * Configures formatters and converters for request processing.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToResponseFormatConverter());
    }

    /**
     * Converter for transforming string values to ResponseFormat enum values.
     */
    @Component
    public static class StringToResponseFormatConverter implements Converter<String, ResponseFormat> {

        @Override
        public ResponseFormat convert(String source) {
            return ResponseFormat.fromString(source);
        }
    }

    /**
     * Serve static files and fallback unknown pages to /.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new SpaFallbackResolver());
    }

    private static class SpaFallbackResolver extends PathResourceResolver {
        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            Resource resource = location.createRelative(resourcePath);
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            return location.createRelative("index.html");
        }
    }

    /**
     * Allow svelte-kit dev serve to use spring api.
     */
    @Override
    @Profile("test")
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
