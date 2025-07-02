package dev.clutcher.security.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigurableSecurityExceptionHandler implements SecurityExceptionHandler {

    private final ObjectMapper objectMapper;
    private final List<String> urls;
    private final int priority;
    private final Function<ErrorInfo, String> errorMapper;
    private final Function<Map<String, Object>, String> jsonMapper;

    public ConfigurableSecurityExceptionHandler(
            List<String> urls, 
            int priority,
            Function<ErrorInfo, String> errorMapper,
            Function<Map<String, Object>, String> jsonMapper) {
        this.objectMapper = new ObjectMapper();
        this.urls = urls != null ? urls : List.of();
        this.priority = priority;
        this.errorMapper = errorMapper;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean canHandle(HttpServletRequest request) {
        if (urls.isEmpty()) {
            return true;
        }
        return matchesUrl(request);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, RuntimeException exception) throws IOException {
        ErrorInfo errorInfo = resolveErrorInfo(exception);
        
        response.setStatus(errorInfo.httpStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = errorMapper.apply(errorInfo);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
        }
    }

    @Override
    public List<String> getUrls() {
        return urls;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public record ErrorInfo(
            int httpStatus,
            String message,
            String code,
            String errorType,
            String classification
    ) {}

    private ErrorInfo resolveErrorInfo(RuntimeException ex) {
        if (ex instanceof AuthenticationException) {
            return new ErrorInfo(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication required",
                    "AUTHENTICATION_ERROR",
                    "AUTHENTICATION_REQUIRED",
                    "UNAUTHENTICATED"
            );
        }

        if (ex instanceof AccessDeniedException) {
            return new ErrorInfo(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied",
                    "ACCESS_DENIED",
                    "ACCESS_DENIED",
                    "FORBIDDEN"
            );
        }

        return new ErrorInfo(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Internal server error",
                "INTERNAL_ERROR",
                "INTERNAL_SERVER_ERROR",
                "INTERNAL_ERROR"
        );
    }

    public static Function<ErrorInfo, String> createDefaultErrorMapper(ObjectMapper objectMapper) {
        return error -> {
            try {
                Map<String, String> response = Map.of(
                        "code", error.code(),
                        "message", error.message()
                );
                return objectMapper.writeValueAsString(response);
            } catch (JsonProcessingException e) {
                return "{\"code\":\"JSON_PROCESSING_ERROR\",\"message\":\"Internal server error\"}";
            }
        };
    }

    public static Function<ErrorInfo, String> createGraphQLErrorMapper(ObjectMapper objectMapper) {
        return error -> {
            try {
                Map<String, Object> errorResponse = Map.of(
                    "errors", List.of(
                        Map.of(
                            "message", error.message(),
                            "extensions", Map.of(
                                "errorType", error.errorType(),
                                "classification", error.classification(),
                                "code", error.code()
                            )
                        )
                    )
                );
                return objectMapper.writeValueAsString(errorResponse);
            } catch (JsonProcessingException e) {
                return "{\"errors\":[{\"message\":\"Internal server error\",\"extensions\":{\"errorType\":\"INTERNAL_SERVER_ERROR\",\"classification\":\"INTERNAL_ERROR\",\"code\":\"JSON_PROCESSING_ERROR\"}}]}";
            }
        };
    }

    public static Function<Map<String, Object>, String> createDefaultJsonMapper(ObjectMapper objectMapper) {
        return map -> {
            try {
                return objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                return "{\"error\":\"JSON_PROCESSING_ERROR\"}";
            }
        };
    }
}