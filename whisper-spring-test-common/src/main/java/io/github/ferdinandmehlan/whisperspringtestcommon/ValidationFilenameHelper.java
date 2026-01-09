package io.github.ferdinandmehlan.whisperspringtestcommon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.TestInfo;

/**
 * Utility class for generating validation file names based on test class hierarchy and method names.
 * This helper creates unique, hierarchical names for validation files that reflect the test structure,
 * making it easier to organize and locate validation files for different test scenarios.
 */
public final class ValidationFilenameHelper {

    private final TestInfo testInfo;

    /**
     * Creates a new ValidationFilenameHelper with the given test information.
     *
     * @param testInfo the JUnit TestInfo containing details about the current test
     */
    public ValidationFilenameHelper(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    /**
     * Generates a hierarchical test name for validation file naming.
     * The name includes the full class hierarchy (from outermost to innermost class)
     * followed by the test method name, separated by forward slashes.
     *
     * @return the formatted test name suitable for file naming
     */
    public String getTestName() {
        List<String> classes = ValidationFilenameHelper.classHierarchy(getTestClass());
        return String.join("/", classes) + "/" + getTestMethod().getName();
    }

    private Method getTestMethod() {
        return testInfo.getTestMethod().orElseThrow();
    }

    private Class<?> getTestClass() {
        return testInfo.getTestClass().orElseThrow();
    }

    private static List<String> classHierarchy(Class<?> aClass) {
        List<String> classHierarchy = new ArrayList<>();
        classHierarchy.add(aClass.getSimpleName());
        Class<?> enclosingClass = aClass.getEnclosingClass();
        while (enclosingClass != null) {
            classHierarchy.add(enclosingClass.getSimpleName());
            enclosingClass = enclosingClass.getEnclosingClass();
        }
        Collections.reverse(classHierarchy);
        return classHierarchy;
    }
}
