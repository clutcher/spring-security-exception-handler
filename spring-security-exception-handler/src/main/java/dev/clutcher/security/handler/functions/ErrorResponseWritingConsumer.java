package dev.clutcher.security.handler.functions;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ErrorResponseWritingConsumer implements BiConsumer<RuntimeException, HttpServletResponse> {

    private final Function<RuntimeException, ErrorResponse> exceptionMapper;

    public ErrorResponseWritingConsumer(Function<RuntimeException, ErrorResponse> exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
    }


    @Override
    public void accept(RuntimeException exception, HttpServletResponse response) {
        ErrorResponse errorResponse = exceptionMapper.apply(exception);

        response.setStatus(errorResponse.status());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.write(errorResponse.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record ErrorResponse(int status, String body) {
    }

}
