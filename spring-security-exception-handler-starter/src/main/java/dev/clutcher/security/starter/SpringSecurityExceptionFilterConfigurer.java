package dev.clutcher.security.starter;

import dev.clutcher.security.filter.SpringSecurityExceptionFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class SpringSecurityExceptionFilterConfigurer extends AbstractHttpConfigurer<SpringSecurityExceptionFilterConfigurer, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) {
        SpringSecurityExceptionFilter springSecurityExceptionFilter = new SpringSecurityExceptionFilter();
        http.addFilterAfter(this.postProcess(springSecurityExceptionFilter), ExceptionTranslationFilter.class);
    }
}
