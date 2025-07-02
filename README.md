# Spring Security Exception Handler

A flexible library for handling Spring Security exceptions with customizable response formats, supporting both REST and GraphQL APIs.

---

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Getting Started](#getting-started)
    - [Installation](#installation)
    - [Basic Usage](#basic-usage)
4. [Implementation Details](#implementation-details)

---

## Overview

By default, Spring Security uses its own `ExceptionTranslationFilter` which converts `AuthenticationException` and `AccessDeniedException` into empty responses with 401 and 403 status codes respectively. While this behavior is sufficient for many applications, there are cases when such behavior is not desired.

For example, in a microservice GraphQL federation environment, there is more value in returning JSON in a GraphQL-compatible format, allowing you to see access denied errors rather than just internal server errors.

**Spring Security Exception Handler** provides a clean and flexible way to handle security exceptions in Spring applications. It allows you to customize how authentication and authorization exceptions are handled and presented to clients, with built-in support for both standard REST APIs and GraphQL endpoints.

### Key Features

- Custom handling of Spring Security exceptions (`AuthenticationException` and `AccessDeniedException`)
- URL pattern-based routing of exceptions to appropriate handlers
- Built-in support for REST and GraphQL response formats
- Flexible builder API for creating custom exception handlers
- Spring Boot auto-configuration with sensible defaults
- Fully customizable through application properties

---

## Project Structure

This project consists of two main libraries:

- **`spring-security-exception-handler`** - Core library with exception handling logic and builder API
- **`spring-security-exception-handler-starter`** - Spring Boot starter with auto-configuration and property support

---

## Getting Started

### Installation

Add the starter dependency to your Spring Boot project:

#### Gradle
```kotlin
implementation("dev.clutcher.spring-security:spring-security-exception-handler-starter:1.0.0")
```

#### Maven
```xml
<dependency>
    <groupId>dev.clutcher.spring-security</groupId>
    <artifactId>spring-security-exception-handler-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

The library provides auto-configuration that automatically sets up default exception handlers for both REST and GraphQL endpoints. Simply add the starter dependency to your project and the library will automatically inject a preconfigured instance of `SpringSecurityExceptionFilterConfigurer` through Spring Boot's auto-configuration mechanism.

The auto-configuration is enabled through `spring.factories` and `SpringSecurityExceptionHandlerAutoConfiguration`, which automatically registers the necessary components without requiring any manual configuration.

That's it! The library will automatically handle Spring Security exceptions with sensible defaults - no additional configuration required.

#### Manual Filter Configuration

If you need more control over the filter configuration or prefer explicit setup, you can manually create and configure the `SpringSecurityExceptionFilter` bean:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private List<SpringSecurityExceptionHandler> exceptionHandlers;

    @Bean
    public SpringSecurityExceptionFilter springSecurityExceptionFilter() {
        return new SpringSecurityExceptionFilter(exceptionHandlers);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                         SpringSecurityExceptionFilter exceptionFilter) throws Exception {
        return http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(exceptionFilter, ExceptionTranslationFilter.class)
            .formLogin(withDefaults())
            .build();
    }
}
```

This approach gives you full control over when and how the filter is added to the security filter chain.

#### Property-Based Configuration

Configure exception handlers through application properties:

```yaml
dev:
  clutcher:
    security:
      handlers:
        default:
          enabled: true
          urls: ["/**"]
          order: 100
        graphql:
          enabled: true
          urls: ["/graphql"]
          order: 0
        custom:
          enabled: true
          urls: ["/api/v2/**"]
          order: 50
```

#### Custom Exception Handler

Create custom exception handlers for specific URL patterns:

```java
@Configuration
public class CustomExceptionHandlerConfig {

    @Bean
    public SpringSecurityExceptionHandler apiV2ExceptionHandler() {
        return SpringSecurityExceptionHandlerBuilder.builder()
            .canHandle(new UrlMatchingPredicate(List.of("/api/v2/**")))
            .handle(new ErrorResponseWritingConsumer(exception -> {
                if (exception instanceof AuthenticationException) {
                    return new ErrorResponseWritingConsumer.ErrorResponse(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "{\"error\":\"Authentication required\",\"code\":\"AUTH_REQUIRED\"}"
                    );
                }
                if (exception instanceof AccessDeniedException) {
                    return new ErrorResponseWritingConsumer.ErrorResponse(
                        HttpServletResponse.SC_FORBIDDEN,
                        "{\"error\":\"Access denied\",\"code\":\"ACCESS_DENIED\"}"
                    );
                }
                return new ErrorResponseWritingConsumer.ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"error\":\"Internal server error\"}"
                );
            }))
            .order(50)
            .build();
    }
}
```

## Implementation Details

The Spring Security Exception Handler library is built around several key components that work together to provide flexible exception handling for Spring Security applications.

### Core Components

The library consists of the following main interfaces and classes:

- **`SpringSecurityExceptionFilter`** - Servlet filter that intercepts Spring Security exceptions and delegates to registered handlers.
- **`SpringSecurityExceptionHandler`** - Main interface for handling security exceptions. Implements `Ordered` to support priority-based handler selection.
- **`SpringSecurityExceptionHandlerBuilder`** - Fluent builder API for creating custom exception handlers with predicates and response writers.
- **`UrlMatchingPredicate`** - Predicate implementation for URL pattern matching using Spring's `AntPathMatcher`.
- **`ErrorResponseWritingConsumer`** - Consumer for writing structured error responses with customizable status codes and content.
- **`ExceptionMappingFunctions`** - Utility class providing pre-built exception mapping functions for common use cases.

### Exception Handling Flow

The exception handling process follows this sequence:

1. Spring Security's authentication or authorization mechanisms throw an `AuthenticationException` or `AccessDeniedException`
2. `SpringSecurityExceptionFilter` intercepts the exception before it reaches Spring Security's default `ExceptionTranslationFilter`
3. The filter iterates through all registered `SpringSecurityExceptionHandler` beans in priority order (based on the `getOrder()` method)
4. The first handler that returns `true` from its `canHandle(HttpServletRequest)` method processes the exception
5. The selected handler writes a custom response to the `HttpServletResponse` using its configured response writer
6. If no handler can process the request, the exception continues to Spring Security's default behavior

### Auto-Configuration

The Spring Boot starter provides auto-configuration through `SpringSecurityExceptionHandlerAutoConfiguration`, which:

- Registers default exception handlers for common scenarios (REST APIs, GraphQL endpoints)
- Configures handlers based on application properties under the `dev.clutcher.security` namespace
- Uses conditional beans to avoid conflicts with custom handler configurations
- Integrates seamlessly with Spring Security's filter chain through `SpringSecurityExceptionFilterConfigurer`

### Property-Based Configuration

The library supports comprehensive configuration through application properties:

```yaml
dev:
  clutcher:
    security:
      handlers:
        default:
          enabled: true          # Enable/disable the handler
          urls: ["/**"]          # URL patterns to match
          order: 100             # Handler priority (lower = higher priority)
        graphql:
          enabled: true
          urls: ["/graphql", "/graphiql"]
          order: 0
```

Each handler configuration supports:
- **enabled** - Boolean flag to enable/disable the handler
- **urls** - List of URL patterns using Ant-style matching
- **order** - Integer defining handler priority (lower values have higher priority)

### Extensibility

The library is designed for extensibility through several extension points:

- **Custom Predicates** - Implement custom logic for determining when a handler should process a request
- **Custom Response Writers** - Create specialized response formats (XML, custom JSON structures, etc.)
- **Handler Composition** - Combine multiple handlers with different priorities and URL patterns
- **Integration Points** - Leverage Spring Boot's conditional configuration to adapt to different application contexts
