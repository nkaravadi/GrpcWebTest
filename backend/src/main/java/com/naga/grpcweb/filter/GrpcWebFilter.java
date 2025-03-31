package com.naga.grpcweb.filter;

import com.naga.grpcweb.fluxconfig.GrpcWebMediaTypeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GrpcWebFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(GrpcWebFilter.class);

    private static final List<MediaType> GRPC_WEB_TYPES = List.of(
            MediaType.valueOf("application/grpc-web"),
            MediaType.valueOf("application/grpc-web-text"),
            MediaType.valueOf("application/grpc-web+proto"),
            MediaType.valueOf("application/grpc-web-text+proto")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        logger.info("Processing gRPC-Web request with Content-Type: {}", contentType);

        if (contentType != null && isGrpcWebContentType(contentType)) {
            logger.info("Processing gRPC-Web request with Content-Type: {}", contentType);
            // Store original content type
            exchange.getAttributes().put("originalContentType", contentType.toString());

            // Rewrite the request to standard gRPC
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header(HttpHeaders.CONTENT_TYPE, "application/grpc")
                    .build();
            logger.debug("Converted Content-Type to: application/grpc");

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        logger.debug("Not a gRPC-Web request, passing through");
        return chain.filter(exchange);
    }

    private boolean isGrpcWebContentType(MediaType contentType) {
        return GRPC_WEB_TYPES.stream()
                .anyMatch(type -> type.isCompatibleWith(contentType));
    }
}
