package com.naga.grpcweb.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Runs right after GrpcWebFilter
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        logger.info("Incoming request: {} {} Headers: {}",
                request.getMethod(),
                request.getPath(),
                request.getHeaders());

        logger.debug("Request body: {}", request.getBody()); // Will log publisher reference

        return chain.filter(exchange).doFinally(signalType -> {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Completed request: {} {} Status: {} ({} ms)",
                    request.getMethod(),
                    request.getPath(),
                    exchange.getResponse().getStatusCode(),
                    duration);
        });
    }
}
