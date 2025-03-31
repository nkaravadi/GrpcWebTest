package com.naga.grpcweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.naga.grpcweb", "net.devh.boot.grpc"})
public class SpringBootGrpcWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootGrpcWebApplication.class, args);
    }
}
