package com.john.project.test.properties.DevelopmentMockModeProperties;

import com.john.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevelopmentMockModePropertiesGetIsDevelopmentMockModeTest extends BaseTest {

    @Test
    public void test() {
        assertTrue(this.developmentMockModeProperties.getIsDevelopmentMockMode());
    }

}
