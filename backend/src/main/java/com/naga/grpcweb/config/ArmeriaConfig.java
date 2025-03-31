package com.naga.grpcweb.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.file.FileService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import com.naga.grpcweb.service.StockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ArmeriaConfig {

    @Bean
    public ArmeriaServerConfigurator armeriaServerConfigurator(StockService stockService) {
        return serverBuilder -> {
            // Set global timeout to disabled (this is the correct way)
            serverBuilder.requestTimeoutMillis(0);

            // Create the gRPC service with web support
            serverBuilder.service(GrpcService.builder()
                            .addService(stockService)
                            .supportedSerializationFormats(GrpcSerializationFormats.values())
                            .enableUnframedRequests(true)
                            .build()/*,
                    // Add service-specific timeout configuration
                    builder -> builder.decorate(
                            (delegate, ctx, req) -> {
                                ctx.setRequestTimeout(Duration.ZERO); // This has to be +ve!
                                return delegate.serve(ctx, req);
                            }) */
            );

            // Add static file serving
            serverBuilder.serviceUnder("/",
                    FileService.builder(ClassLoader.getSystemClassLoader(), "static")
                            .maxCacheEntries(100)
                            .build());

            // Add logging
            serverBuilder.decorator(LoggingService.newDecorator());

            // Optional: Add API docs
            serverBuilder.serviceUnder("/docs", new DocService());
        };
    }

    @Configuration
    public static class TracingConfig {
        @Bean
        public Tracing tracing() {
            return Tracing.newBuilder()
                    .currentTraceContext(RequestContextCurrentTraceContext.ofDefault())
                    .build();
        }

        @Bean
        public GrpcTracing grpcTracing(Tracing tracing) {
            return GrpcTracing.create(tracing);
        }

        @Bean
        public ArmeriaServerConfigurator tracingConfigurator(Tracing tracing) {
            return serverBuilder -> {
                serverBuilder.decorator(BraveService.newDecorator(tracing));
            };
        }
    }
}
