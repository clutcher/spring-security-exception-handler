package dev.clutcher.security.filter;

import dev.clutcher.security.handler.SpringSecurityExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpringSecurityExceptionFilter extends OncePerRequestFilter {

    private List<SpringSecurityExceptionHandler> handlers;

    public SpringSecurityExceptionFilter(List<SpringSecurityExceptionHandler> handlers) {
        this.handlers = new ArrayList<>(handlers);
    }

    public SpringSecurityExceptionFilter() {
        this.handlers = new ArrayList<>();
    }

    @Autowired
    public void setHandlers(List<SpringSecurityExceptionHandler> handlers) {
        this.handlers = new ArrayList<>(handlers);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException ex) {
            for (SpringSecurityExceptionHandler handler : handlers) {
                if (handler.canHandle(request)) {
                    handler.handle(ex, response);
                    return;
                }
            }
            throw ex;
        }
    }
}