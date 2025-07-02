package dev.clutcher.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class SpringSecurityExceptionHandlerBuilder {

    private Predicate<HttpServletRequest> canHandlePredicate;
    private BiConsumer<RuntimeException, HttpServletResponse> handleFunction;
    private int order = 0;

    private SpringSecurityExceptionHandlerBuilder() {
    }

    public static SpringSecurityExceptionHandlerBuilder builder() {
        return new SpringSecurityExceptionHandlerBuilder();
    }

    public SpringSecurityExceptionHandlerBuilder canHandle(Predicate<HttpServletRequest> canHandlePredicate) {
        this.canHandlePredicate = canHandlePredicate;
        return this;
    }

    public SpringSecurityExceptionHandlerBuilder handle(BiConsumer<RuntimeException, HttpServletResponse> handleFunction) {
        this.handleFunction = handleFunction;
        return this;
    }

    public SpringSecurityExceptionHandlerBuilder order(int order) {
        this.order = order;
        return this;
    }

    public SpringSecurityExceptionHandler build() {
        return new SpringSecurityExceptionHandler() {
            @Override
            public boolean canHandle(HttpServletRequest request) {
                return canHandlePredicate.test(request);
            }

            @Override
            public void handle(RuntimeException exception, HttpServletResponse response) {
                handleFunction.accept(exception, response);
            }

            @Override
            public int getOrder() {
                return order;
            }
        };
    }
}
