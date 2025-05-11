package com.john.project.test.properties.DevelopmentMockModeProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import com.john.project.test.common.BaseTest.BaseTest;

public class DevelopmentMockModePropertiesGetIsCypressTestEnvironmentTest extends BaseTest {

    @Test
    public void test() {
        assertFalse(this.developmentMockModeProperties.getIsCypressTestEnvironment());
    }

}
