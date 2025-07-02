package dev.clutcher.security.handler.functions;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphqlJsonBodyExceptionMappingTest {

    @Test
    void shouldConvertAuthenticationException() {
        // Given
        Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> mapper =
                ExceptionMappingFunctions.graphqlJsonBodyExceptionMapping();
        AuthenticationException exception = new BadCredentialsException("Invalid credentials");

        // When
        ErrorResponseWritingConsumer.ErrorResponse response = mapper.apply(exception);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.status());
        assertEquals("{\"errors\":[{\"message\":\"Authentication required\",\"extensions\":{\"errorType\":\"AUTHENTICATION_REQUIRED\",\"classification\":\"UNAUTHENTICATED\",\"code\":\"AUTHENTICATION_ERROR\"}}]}", response.body());
    }

    @Test
    void shouldConvertAccessDeniedException() {
        // Given
        Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> mapper =
                ExceptionMappingFunctions.graphqlJsonBodyExceptionMapping();
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // When
        ErrorResponseWritingConsumer.ErrorResponse response = mapper.apply(exception);

        // Then
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.status());
        assertEquals("{\"errors\":[{\"message\":\"Access denied\",\"extensions\":{\"errorType\":\"ACCESS_DENIED\",\"classification\":\"FORBIDDEN\",\"code\":\"ACCESS_DENIED\"}}]}", response.body());
    }

    @Test
    void shouldConvertNonSpringSecurityException() {
        // Given
        Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> mapper =
                ExceptionMappingFunctions.graphqlJsonBodyExceptionMapping();
        RuntimeException exception = new RuntimeException("Some error");

        // When
        ErrorResponseWritingConsumer.ErrorResponse response = mapper.apply(exception);

        // Then
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.status());
        assertEquals("{\"errors\":[{\"message\":\"Internal server error\",\"extensions\":{\"errorType\":\"INTERNAL_SERVER_ERROR\",\"classification\":\"INTERNAL_ERROR\",\"code\":\"INTERNAL_ERROR\"}}]}", response.body());
    }
}