package dev.clutcher.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringSecurityExceptionHandlerBuilderTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Predicate<HttpServletRequest> canHandlePredicate;

    @Mock
    private BiConsumer<RuntimeException, HttpServletResponse> handleFunction;

    @Test
    void shouldCreateHandlerInstance() {
        // Given
        when(canHandlePredicate.test(request)).thenReturn(true);

        // When
        SpringSecurityExceptionHandler handler = SpringSecurityExceptionHandlerBuilder.builder()
                                                                                      .canHandle(canHandlePredicate)
                                                                                      .handle(handleFunction)
                                                                                      .order(42)
                                                                                      .build();

        // Then
        boolean canHandle = handler.canHandle(request);
        assertTrue(canHandle);

        int order = handler.getOrder();
        assertEquals(42, order);
    }

}
