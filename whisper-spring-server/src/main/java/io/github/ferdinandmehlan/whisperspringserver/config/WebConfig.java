package io.github.ferdinandmehlan.whisperspringserver.config;

import io.github.ferdinandmehlan.whisperspringserver.inference.ResponseFormat;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
}
