package dev.clutcher.security.handler.functions;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonBodyExceptionMappingTest {

    @Test
    void shouldConvertAuthenticationException() {
        // Given
        Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> mapper =
                ExceptionMappingFunctions.jsonBodyExceptionMapping();
        AuthenticationException exception = new BadCredentialsException("Invalid credentials");

        // When
        ErrorResponseWritingConsumer.ErrorResponse response = mapper.apply(exception);

        // Then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.status());
        assertEquals("{\"code\":\"AUTHENTICATION_ERROR\",\"message\":\"Authentication required\"}", response.body());
    }

    @Test
    void shouldConvertAccessDeniedException() {
        // Given
        Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> mapper =
                ExceptionMappingFunctions.jsonBodyExceptionMapping();
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // When
        ErrorResponseWritingConsumer.ErrorResponse response = mapper.apply(exception);

        // Then
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.status());
        assertEquals("{\"code\":\"ACCESS_DENIED\",\"message\":\"Access denied\"}", response.body());
    }

    @Test
    void shouldConvertNonSpringSecurityException() {
        // Given
        Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> mapper =
                ExceptionMappingFunctions.jsonBodyExceptionMapping();
        RuntimeException exception = new RuntimeException("Some error");

        // When
        ErrorResponseWritingConsumer.ErrorResponse response = mapper.apply(exception);

        // Then
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.status());
        assertEquals("{\"code\":\"INTERNAL_ERROR\",\"message\":\"Internal server error\"}", response.body());
    }
}