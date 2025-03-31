// frontend/src/StockDataService.js

import { StockServiceClient } from './stock_grpc_web_pb';
import { StockRequest } from './stock_pb';

const client = new StockServiceClient('http://localhost:8080',
    null, {
        withCredentials: false,
        unaryInterceptors: [],
        streamInterceptors: []

    }
); // Adjust URL if needed

export const getStockUpdates = (symbol, onUpdate) => {
    const request = new StockRequest();
    request.setSymbol(symbol);

    const stream = client.getStockUpdates(request, {
        'Content-Type': 'application/grpc-web-text',
        'Accept': 'application/grpc-web-text',
        'X-Grpc-Web': '1'
    });

    // stream.on('data', (response) => {
    //     onUpdate(response.toObject());
    // });

    stream.on('data', (response) => {
        onUpdate({
            symbol: response.getSymbol(),
            price: response.getPrice(),
            timestamp: response.getTimestamp()
        });
    });

    stream.on('error', (err) => {
        console.error('gRPC stream error:', err);
    });

    stream.on('end', () => {
        console.log('Stream ended');
    });

    return stream; // Return the stream, so you can cancel it later
};
