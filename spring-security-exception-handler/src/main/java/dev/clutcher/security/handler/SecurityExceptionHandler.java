package dev.clutcher.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.List;

public interface SecurityExceptionHandler {

    boolean canHandle(HttpServletRequest request);

    void handle(HttpServletRequest request, HttpServletResponse response, RuntimeException exception) throws IOException;

    default boolean matchesUrl(HttpServletRequest request) {
        List<String> urls = getUrls();
        if (urls.isEmpty()) {
            return false;
        }

        PathMatcher pathMatcher = new AntPathMatcher();
        String requestUri = request.getRequestURI();
        return urls.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    default int getPriority() {
        return 0;
    }

    default List<String> getUrls() {
        return List.of();
    }
}
