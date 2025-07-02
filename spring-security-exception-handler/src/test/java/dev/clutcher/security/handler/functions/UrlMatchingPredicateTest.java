package dev.clutcher.security.handler.functions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlMatchingPredicateTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void shouldNotMatchAnyUrlWhenNoPatternsAreGiven() {
        // Given
        UrlMatchingPredicate predicate = new UrlMatchingPredicate(Collections.emptyList());
        when(request.getRequestURI()).thenReturn("/api/users");

        // When
        boolean result = predicate.test(request);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldMatchWhenExactPatternIsUsed() {
        // Given
        UrlMatchingPredicate predicate = new UrlMatchingPredicate(List.of("/api/users"));
        when(request.getRequestURI()).thenReturn("/api/users");

        // When
        boolean result = predicate.test(request);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldMatchWhenWildcardPatternIsUsed() {
        // Given
        UrlMatchingPredicate predicate = new UrlMatchingPredicate(List.of("/api/**"));
        when(request.getRequestURI()).thenReturn("/api/users/123");

        // When
        boolean result = predicate.test(request);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldMatchWhenMultipleMatchersProvided() {
        // Given
        UrlMatchingPredicate predicate = new UrlMatchingPredicate(
                Arrays.asList("/api/users/**", "/api/products/**", "/graphql")
        );
        when(request.getRequestURI()).thenReturn("/graphql");

        // When
        boolean result = predicate.test(request);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotMatch() {
        // Given
        UrlMatchingPredicate predicate = new UrlMatchingPredicate(
                Arrays.asList("/api/users/**", "/api/products/**", "/graphql")
        );
        when(request.getRequestURI()).thenReturn("/api/orders/789");

        // When
        boolean result = predicate.test(request);

        // Then
        assertFalse(result);
    }

}
