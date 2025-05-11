package com.john.project.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class DevelopmentMockModeProperties {

    @Value("${properties.is.development.mock.mode}")
    private Boolean isDevelopmentMockMode;

    @Value("${org.springframework.boot.test.context.SpringBootTestContextBootstrapper:false}")
    private Boolean isUnitTestEnvironment;

    @Value("${properties.is.cypress.mock.mode.:false}")
    private Boolean isCypressTestEnvironment;

}

