package com.john.project.common.config;

import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.stereotype.Component;

@EnableResilientMethods
@Component
public class ServiceRetryConfig {

}