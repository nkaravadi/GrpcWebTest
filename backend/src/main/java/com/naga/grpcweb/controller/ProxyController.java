package com.naga.grpcweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);

    private final WebClient webClient;
    private final ResourceLoader resourceLoader;

    public ProxyController(WebClient.Builder webClientBuilder, ResourceLoader resourceLoader) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:9090")
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
                })
                .build();
        this.resourceLoader = resourceLoader;
    }

    @PostMapping(
            path = "/**",
            consumes = {
                    "application/grpc-web",
                    "application/grpc-web-text",
                    "application/grpc-web+proto",
                    "application/grpc-web-text+proto"
            },
            produces = {
                    "application/grpc-web",
                    "application/grpc-web-text",
                    "application/grpc-web+proto",
                    "application/grpc-web-text+proto"
            }
    )
    public Flux<byte[]> handleGrpcWeb(ServerWebExchange exchange) {
        logger.info("Handling gRPC-Web request to: {}", exchange.getRequest().getPath());

        String originalContentType = exchange.getAttribute("originalContentType");

        return webClient.method(exchange.getRequest().getMethod())
                .uri(exchange.getRequest().getPath().value())
                .headers(headers -> {
                    logger.debug("Forwarding headers: {}", exchange.getRequest().getHeaders());
                    headers.addAll(exchange.getRequest().getHeaders());
                    headers.set(HttpHeaders.CONTENT_TYPE, "application/grpc+proto");
                })
                .body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody()))
                .exchangeToFlux(clientResponse -> {
                    logger.debug("Received gRPC response with status: {}", clientResponse.statusCode());
                    return clientResponse.bodyToFlux(byte[].class);
                })
                .doOnError(e -> logger.error("gRPC forwarding error", e));
    }

    @GetMapping("/**")
    public Mono<ResponseEntity<Resource>> serveStaticFiles(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        if (path.equals("/")) {
            path = "/index.html";
        }

        Resource resource = resourceLoader.getResource("classpath:static" + path);

        try {
            if (resource.exists() && resource.isReadable()) {
                return Mono.just(ResponseEntity.ok()
                        .contentType(getMediaType(path))
                        .body(resource));
            } else {
                // Fallback to index.html for SPA routing
                Resource index = resourceLoader.getResource("classpath:static/index.html");
                return Mono.just(ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(index));
            }
        } catch (Exception e) {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

    private MediaType getMediaType(String filename) {
        if (filename.endsWith(".html")) return MediaType.TEXT_HTML;
        if (filename.endsWith(".js")) return MediaType.valueOf("application/javascript");
        if (filename.endsWith(".css")) return MediaType.valueOf("text/css");
        if (filename.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (filename.endsWith(".svg")) return MediaType.valueOf("image/svg+xml");
        if (filename.endsWith(".json")) return MediaType.APPLICATION_JSON;
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
