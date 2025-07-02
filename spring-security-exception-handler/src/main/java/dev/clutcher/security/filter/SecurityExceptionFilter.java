package dev.clutcher.security.filter;

import dev.clutcher.security.handler.SecurityExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class SecurityExceptionFilter extends OncePerRequestFilter {

    private final List<SecurityExceptionHandler> handlers;

    public SecurityExceptionFilter(List<SecurityExceptionHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException ex) {
            
            for (SecurityExceptionHandler handler : handlers) {
                if (handler.canHandle(request)) {
                    handler.handle(request, response, ex);
                    return;
                }
            }
            throw ex;
        }
    }
}