package com.naga.grpcweb.controller;

import com.naga.grpcweb.service.StockService;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

//@Configuration
public class GrpcServerConfig {

////    @Value("${grpc.server.port:9090}")
//    private int grpcPort;
//
////    @Bean
//    public Server grpcServer(StockService stockService) throws IOException {
//        return NettyServerBuilder.forPort(grpcPort)
//                .addService(stockService)
//                .maxInboundMessageSize(16 * 1024 * 1024) // 16MB
//                .build()
//                .start();
//
////        return NettyServerBuilder.forPort(grpcPort)
////                .addService(stockService)
////                .intercept(new ServerInterceptor() {
////                    @Override
////                    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
////                            ServerCall<ReqT, RespT> call,
////                            Metadata headers,
////                            ServerCallHandler<ReqT, RespT> next) {
////
////                        // Set CORS headers for gRPC responses
////                        headers.put(Metadata.Key.of("Access-Control-Allow-Origin", Metadata.ASCII_STRING_MARSHALLER), "*");
////                        return next.startCall(call, headers);
////                    }
////                })
////                .build()
////                .start();
//    }
//
////    @Autowired
////    private StockService stockService;
////
////    private Server server;
////
////    //Logger
////    private static final Logger logger = LoggerFactory.getLogger(GrpcServerConfig.class);
////
////    @PostConstruct
////    public void startGrpcServer() throws IOException {
////        logger.info("Starting gRPC server..");
////        server = ServerBuilder.forPort(9090).addService(stockService).build();
////        server.start();
////        logger.info("Started gRPC server at port 9090");
////    }
////
////    @PreDestroy
////    public void stopGrpcServer() {
////        if (server != null) {
////            logger.info("Shutting down gRPC server..");
////            server.shutdown();
////            logger.info("gRPC server shut down");
////            return;
////        }
////        logger.info("gRPC server was never started");
////    }
}
