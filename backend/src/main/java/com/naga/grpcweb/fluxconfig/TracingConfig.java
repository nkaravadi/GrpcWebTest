package com.naga.grpcweb.fluxconfig;

import brave.Tracing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    @Bean
    public Tracing tracing() {
        // Minimal no-op tracing configuration
        return Tracing.newBuilder()
            .build();
    }
}
