package dev.clutcher.security.starter;

import dev.clutcher.security.handler.SpringSecurityExceptionHandler;
import dev.clutcher.security.handler.SpringSecurityExceptionHandlerBuilder;
import dev.clutcher.security.handler.functions.ErrorResponseWritingConsumer;
import dev.clutcher.security.handler.functions.ExceptionMappingFunctions;
import dev.clutcher.security.handler.functions.UrlMatchingPredicate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableConfigurationProperties(SecurityExceptionHandlerProperties.class)
public class SpringSecurityExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "defaultSecurityExceptionHandler")
    public SpringSecurityExceptionHandler defaultSecurityExceptionHandler(SecurityExceptionHandlerProperties properties) {
        SecurityExceptionHandlerProperties.HandlerConfig config = properties.getHandlers().get("default");

        if (isEnabledHandler(config)) {
            return null;
        }

        return SpringSecurityExceptionHandlerBuilder.builder()
                                                    .canHandle(new UrlMatchingPredicate(config.getUrls()))
                                                    .handle(
                                                            new ErrorResponseWritingConsumer(
                                                                    ExceptionMappingFunctions.jsonBodyExceptionMapping()
                                                            )
                                                    )
                                                    .order(config.getOrder())
                                                    .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "graphqlSecurityExceptionHandler")
    public SpringSecurityExceptionHandler graphqlSecurityExceptionHandler(SecurityExceptionHandlerProperties properties) {
        SecurityExceptionHandlerProperties.HandlerConfig config = properties.getHandlers().get("graphql");

        if (isEnabledHandler(config)) {
            return null;
        }

        return SpringSecurityExceptionHandlerBuilder.builder()
                                                    .canHandle(new UrlMatchingPredicate(config.getUrls()))
                                                    .handle(
                                                            new ErrorResponseWritingConsumer(
                                                                    ExceptionMappingFunctions.graphqlJsonBodyExceptionMapping()
                                                            )
                                                    )
                                                    .order(config.getOrder())
                                                    .build();
    }

    private boolean isEnabledHandler(SecurityExceptionHandlerProperties.HandlerConfig config) {
        return config == null || !config.isEnabled();
    }

}