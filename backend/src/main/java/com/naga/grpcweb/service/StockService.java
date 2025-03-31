package com.naga.grpcweb.service;

import com.naga.grpcweb.StockRequest;
import com.naga.grpcweb.StockServiceGrpc;
import com.naga.grpcweb.StockUpdate;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GrpcService
public class StockService extends StockServiceGrpc.StockServiceImplBase {

    @Override
    public void getStockUpdates(StockRequest request, StreamObserver<StockUpdate> responseObserver) {
        String symbol = request.getSymbol();
        Random random = new Random();

        // Use a single threaded executor instead of fixed rate
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            try {
                for (int i = 0; i < 10; i++) { // Send 10 updates then complete
                    double price = 100 + random.nextDouble() * 50;
                    StockUpdate update = StockUpdate.newBuilder()
                            .setSymbol(symbol)
                            .setPrice(price)
                            .setTimestamp((int) (System.currentTimeMillis() / 1000))
                            .build();

                    responseObserver.onNext(update);
                    Thread.sleep(1000); // 1 second delay
                }
                responseObserver.onCompleted();
            } catch (Exception e) {
                responseObserver.onError(e);
            } finally {
                executor.shutdown();
            }
        });

        // Cleanup on cancellation
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdownNow();
            responseObserver.onCompleted();
        }));
    }
}
