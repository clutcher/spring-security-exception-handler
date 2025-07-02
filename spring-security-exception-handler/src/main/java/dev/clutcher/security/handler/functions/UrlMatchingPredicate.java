package dev.clutcher.security.handler.functions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;
import java.util.function.Predicate;

public class UrlMatchingPredicate implements Predicate<HttpServletRequest> {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final List<String> urlPatterns;

    public UrlMatchingPredicate(List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public boolean test(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return urlPatterns.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, requestUri));
    }

}
