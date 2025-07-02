package dev.clutcher.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;

import java.io.IOException;

public interface SpringSecurityExceptionHandler extends Ordered {

    boolean canHandle(HttpServletRequest request);

    void handle(RuntimeException exception, HttpServletResponse response) throws IOException;

}
