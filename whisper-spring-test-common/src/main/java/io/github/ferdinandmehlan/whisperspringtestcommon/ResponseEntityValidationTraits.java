package io.github.ferdinandmehlan.whisperspringtestcommon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cronn.assertions.validationfile.junit5.JUnit5ValidationFileAssertions;
import de.cronn.assertions.validationfile.normalization.SimpleRegexReplacement;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

/**
 * Interface providing default methods for validating Spring {@link ResponseEntity} objects using validation files.
 * This interface extends {@link JUnit5ValidationFileAssertions} to provide assertion methods that compare
 * response entities against expected validation files, with support for JSON serialization and normalization.
 */
public interface ResponseEntityValidationTraits extends JUnit5ValidationFileAssertions {

    /**
     * Provides a default {@link ObjectMapper} instance for JSON serialization and deserialization.
     * This mapper is used when converting response bodies to JSON format for validation file comparisons.
     *
     * @return a new ObjectMapper instance
     */
    default ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Provides a default {@link ValidationNormalizer} that masks date values in validation files.
     * This normalizer replaces date patterns with a masked placeholder to avoid test failures
     * due to dynamic date values.
     *
     * @return a ValidationNormalizer that masks dates
     */
    default ValidationNormalizer defaultValidationNormalizer() {
        return new SimpleRegexReplacement("Date: .*", "Date: [MASKED]");
    }

    /**
     * Asserts that the response headers match the content of a validation file.
     * Uses the default suffix "headers" for the validation file name.
     *
     * @param response the ResponseEntity whose headers should be validated
     */
    default void assertHeadersWithFile(ResponseEntity<?> response) {
        assertHeadersWithFile(response, null);
    }

    /**
     * Asserts that the response headers match the content of a validation file with a custom suffix.
     * The headers are formatted as "key: value" pairs, one per line.
     *
     * @param response the ResponseEntity whose headers should be validated
     * @param suffix the suffix to append to the validation file name, or null for default "headers"
     */
    default void assertHeadersWithFile(ResponseEntity<?> response, String suffix) {
        String actual = response.getHeaders().headerSet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
        suffix = suffix != null ? suffix : "headers";
        assertWithFileWithSuffix(actual, defaultValidationNormalizer(), suffix);
    }

    /**
     * Asserts that the complete response (including HTTP status and body) matches a validation file.
     * Uses no suffix for the validation file name.
     *
     * @param response the ResponseEntity to validate completely
     */
    default void assertWithFileIncludingHttpStatus(ResponseEntity<?> response) {
        assertWithFileIncludingHttpStatus(response, null);
    }

    /**
     * Asserts that the complete response (including HTTP status and body) matches a validation file with a custom suffix.
     * The response body is serialized to JSON if it's not already a string, and null bodies are handled appropriately.
     *
     * @param response the ResponseEntity to validate completely
     * @param suffix the suffix to append to the validation file name, or null for no suffix
     */
    default void assertWithFileIncludingHttpStatus(ResponseEntity<?> response, String suffix) {
        String httpStatus = "// HTTP " + response.getStatusCode() + "\n";
        Object body = response.getBody();
        if (body == null) {
            assertWithFileWithSuffix(httpStatus + "// --- no body ---", suffix);
        } else if (body instanceof String value) {
            assertWithFileWithSuffix(httpStatus + value, suffix);
        } else {
            assertWithJson5FileWithSuffix(httpStatus + toJson(body), defaultValidationNormalizer(), suffix);
        }
    }

    private String toJson(Object actual) {
        try {
            return objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(actual);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
