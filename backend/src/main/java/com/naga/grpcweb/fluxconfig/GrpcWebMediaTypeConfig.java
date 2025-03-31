package com.naga.grpcweb.fluxconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class GrpcWebMediaTypeConfig implements WebFluxConfigurer {

    public static final MediaType GRPC_WEB = MediaType.valueOf("application/grpc-web");
    public static final MediaType GRPC_WEB_TEXT = MediaType.valueOf("application/grpc-web-text");
    public static final MediaType GRPC_WEB_PROTO = MediaType.valueOf("application/grpc-web+proto");
    public static final MediaType GRPC_WEB_TEXT_PROTO = MediaType.valueOf("application/grpc-web-text+proto");

    @Override
    public void configureContentTypeResolver(RequestedContentTypeResolverBuilder builder) {
        builder.headerResolver();
    }
}
