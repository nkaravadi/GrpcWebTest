package com.naga.grpcweb.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext;
import com.linecorp.armeria.server.brave.BraveService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    @Bean
    public Tracing tracing() {
        return Tracing.newBuilder()
                .currentTraceContext(RequestContextCurrentTraceContext.ofDefault())
//                .spanReporter(zipkinSpanReporter())
                .build();
    }

//    @Bean
//    public Reporter<Span> zipkinSpanReporter() {
//        return AsyncReporter.create(OkHttpSender.create("http://your-zipkin-server:9411/api/v2/spans"));
//    }

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
