package dev.clutcher.security.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.clutcher.security.filter.SecurityExceptionFilter;
import dev.clutcher.security.handler.ConfigurableSecurityExceptionHandler;
import dev.clutcher.security.handler.SecurityExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@AutoConfiguration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(SecurityExceptionHandlerProperties.class)
public class StarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "defaultSecurityExceptionHandler")
    @Order
    public SecurityExceptionHandler defaultSecurityExceptionHandler(SecurityExceptionHandlerProperties properties, ObjectMapper objectMapper) {
        
        SecurityExceptionHandlerProperties.HandlerConfig config = properties.getHandlers().get("default");
        
        if (isConfigInvalid(config)) {
            return null;
        }

        Function<ConfigurableSecurityExceptionHandler.ErrorInfo, String> errorMapper =
                ConfigurableSecurityExceptionHandler.createDefaultErrorMapper(objectMapper);

        Function<Map<String, Object>, String> jsonMapper = 
                ConfigurableSecurityExceptionHandler.createDefaultJsonMapper(objectMapper);

        return new ConfigurableSecurityExceptionHandler(
            config.getUrls(), 
            config.getPriority(),
            errorMapper,
            jsonMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean(name = "graphqlSecurityExceptionHandler")
    @Order
    public SecurityExceptionHandler graphqlSecurityExceptionHandler(SecurityExceptionHandlerProperties properties, ObjectMapper objectMapper) {
        
        SecurityExceptionHandlerProperties.HandlerConfig config = properties.getHandlers().get("graphql");
        
        if (isConfigInvalid(config)) {
            return null;
        }

        Function<ConfigurableSecurityExceptionHandler.ErrorInfo, String> errorMapper =
                ConfigurableSecurityExceptionHandler.createGraphQLErrorMapper(objectMapper);

        Function<Map<String, Object>, String> jsonMapper = 
                ConfigurableSecurityExceptionHandler.createDefaultJsonMapper(objectMapper);

        return new ConfigurableSecurityExceptionHandler(
            config.getUrls(),
            config.getPriority(),
            errorMapper,
            jsonMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean(SecurityExceptionFilter.class)
    public SecurityExceptionFilter securityExceptionFilter(ObjectProvider<SecurityExceptionHandler> handlersProvider) {
        
        List<SecurityExceptionHandler> handlers = handlersProvider.orderedStream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(SecurityExceptionHandler::getPriority))
                .toList();

        if (handlers.isEmpty()) {
            throw new IllegalStateException("No enabled SecurityExceptionHandler beans found. Please ensure proper configuration with enabled: true in application.yml");
        }

        return new SecurityExceptionFilter(handlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public List<SecurityExceptionFilter> securityExceptionFilters(SecurityExceptionFilter securityExceptionFilter) {
        return List.of(securityExceptionFilter);
    }

    @Bean
    public SecurityExceptionFilterConfigurer securityExceptionFilterConfigurer(List<SecurityExceptionFilter> securityFilters) {
        return new SecurityExceptionFilterConfigurer(securityFilters);
    }

    public static class SecurityExceptionFilterConfigurer extends AbstractHttpConfigurer<SecurityExceptionFilterConfigurer, HttpSecurity> {

        private final List<SecurityExceptionFilter> securityFilters;

        public SecurityExceptionFilterConfigurer(List<SecurityExceptionFilter> securityFilters) {
            this.securityFilters = securityFilters;
        }

        @Override
        public void configure(HttpSecurity http) {
            if (securityFilters.isEmpty()) {
                return;
            }

            for (SecurityExceptionFilter filter : securityFilters) {
                http.addFilterAfter(filter, ExceptionTranslationFilter.class);
            }
        }
    }

    private boolean isConfigInvalid(SecurityExceptionHandlerProperties.HandlerConfig config) {
        return config == null || !config.isEnabled();
    }

}