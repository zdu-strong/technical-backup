package com.john.project.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.UrlHandlerFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebPathMatchTrailingSlashConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<UrlHandlerFilter> urlHandlerFilterRegistration() {
        UrlHandlerFilter filter = UrlHandlerFilter
                .trailingSlashHandler("/**")
                .redirect(HttpStatus.PERMANENT_REDIRECT)
                .build();
        FilterRegistrationBean<UrlHandlerFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
