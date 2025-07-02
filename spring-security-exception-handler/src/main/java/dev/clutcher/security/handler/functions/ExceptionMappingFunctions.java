package dev.clutcher.security.handler.functions;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.function.Function;

public class ExceptionMappingFunctions {

    public static Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> jsonBodyExceptionMapping() {
        return exception -> {
            if (exception instanceof AuthenticationException) {
                return new ErrorResponseWritingConsumer.ErrorResponse(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "{\"code\":\"AUTHENTICATION_ERROR\",\"message\":\"Authentication required\"}"
                );
            }
            if (exception instanceof AccessDeniedException) {
                return new ErrorResponseWritingConsumer.ErrorResponse(
                        HttpServletResponse.SC_FORBIDDEN,
                        "{\"code\":\"ACCESS_DENIED\",\"message\":\"Access denied\"}"
                );
            }
            return new ErrorResponseWritingConsumer.ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"code\":\"INTERNAL_ERROR\",\"message\":\"Internal server error\"}"
            );
        };
    }

    public static Function<RuntimeException, ErrorResponseWritingConsumer.ErrorResponse> graphqlJsonBodyExceptionMapping() {
        return exception -> {
            if (exception instanceof AuthenticationException) {
                return new ErrorResponseWritingConsumer.ErrorResponse(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "{\"errors\":[{\"message\":\"Authentication required\",\"extensions\":{\"errorType\":\"AUTHENTICATION_REQUIRED\",\"classification\":\"UNAUTHENTICATED\",\"code\":\"AUTHENTICATION_ERROR\"}}]}"
                );
            }
            if (exception instanceof AccessDeniedException) {
                return new ErrorResponseWritingConsumer.ErrorResponse(
                        HttpServletResponse.SC_FORBIDDEN,
                        "{\"errors\":[{\"message\":\"Access denied\",\"extensions\":{\"errorType\":\"ACCESS_DENIED\",\"classification\":\"FORBIDDEN\",\"code\":\"ACCESS_DENIED\"}}]}"
                );
            }
            return new ErrorResponseWritingConsumer.ErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "{\"errors\":[{\"message\":\"Internal server error\",\"extensions\":{\"errorType\":\"INTERNAL_SERVER_ERROR\",\"classification\":\"INTERNAL_ERROR\",\"code\":\"INTERNAL_ERROR\"}}]}"
            );
        };
    }


}
