package io.github.ferdinandmehlan.whisperspringtestcommon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * Abstract base class for tests that provides common functionality for validation file assertions.
 * This class implements {@link ResponseEntityValidationTraits} and sets up a {@link ValidationFilenameHelper}
 * to generate appropriate test names for validation files.
 */
public abstract class BaseTest implements ResponseEntityValidationTraits {

    private ValidationFilenameHelper validationFilenameHelper;

    /**
     * Sets up the validation filename helper before each test execution.
     * This method initializes the {@link ValidationFilenameHelper} with the current test information
     * to enable proper naming of validation files.
     *
     * @param testInfo the JUnit test information for the current test
     */
    @BeforeEach
    void storeTestInfo(TestInfo testInfo) {
        this.validationFilenameHelper = new ValidationFilenameHelper(testInfo);
    }

    /**
     * Returns the test name for validation file naming purposes.
     * The test name includes the class hierarchy and method name to ensure unique validation files.
     *
     * @return the formatted test name
     */
    @Override
    public String getTestName() {
        return validationFilenameHelper.getTestName();
    }
}
