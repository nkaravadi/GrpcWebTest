package com.naga.grpcweb.fluxconfig;

import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class  GrpcConfig {
    @Bean
    public GrpcServerConfigurer grpcServerConfigurer() {
        return serverBuilder -> {
            serverBuilder.maxInboundMessageSize(16 * 1024 * 1024); // 16MB
        };
    }
}
