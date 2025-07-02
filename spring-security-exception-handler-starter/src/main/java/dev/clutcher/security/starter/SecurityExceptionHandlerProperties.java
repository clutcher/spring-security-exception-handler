package dev.clutcher.security.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "dev.clutcher.security")
public class SecurityExceptionHandlerProperties {

    private Map<String, HandlerConfig> handlers = createDefaultHandlers();

    public Map<String, HandlerConfig> getHandlers() {
        return handlers;
    }

    public void setHandlers(Map<String, HandlerConfig> handlers) {
        Map<String, HandlerConfig> mergedHandlers = createDefaultHandlers();
        if (handlers != null) {
            mergedHandlers.putAll(handlers);
        }
        this.handlers = mergedHandlers;
    }

    private Map<String, HandlerConfig> createDefaultHandlers() {
        Map<String, HandlerConfig> defaults = new HashMap<>();
        
        //Default handler
        HandlerConfig defaultHandler = new HandlerConfig();
        defaultHandler.setEnabled(true);
        defaultHandler.setUrls(List.of("/**"));
        defaultHandler.setOrder(100);
        defaults.put("default", defaultHandler);
        
        //GraphQL handler
        HandlerConfig graphqlHandler = new HandlerConfig();
        graphqlHandler.setEnabled(true);
        graphqlHandler.setUrls(List.of("/graphql"));
        graphqlHandler.setOrder(0);
        defaults.put("graphql", graphqlHandler);
        
        return defaults;
    }

    public static class HandlerConfig {
        private boolean enabled = false;
        private List<String> urls = List.of();
        private int order = 100;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}