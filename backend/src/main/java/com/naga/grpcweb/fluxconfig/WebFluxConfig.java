package com.naga.grpcweb.fluxconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // Increase buffer size for gRPC messages
        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 16MB
    }

    @Override
    public void configureContentTypeResolver(RequestedContentTypeResolverBuilder builder) {
        // Use default resolution strategy (header-based)
        builder.headerResolver();
    }
}
