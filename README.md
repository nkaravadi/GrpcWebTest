# gRPC-Web Stock Streaming Application

**Objective:** Demonstrate real-time, server-to-client stock price streaming from a Java backend to a web browser using gRPC-Web, avoiding polling or WebSockets.
## Table of Contents
- [How it Works: gRPC-Web Streaming](#how-it-works-grpc-web-streaming)
- [Technology Choice: Armeria](#technology-choice-armeria)
- [Architecture](#architecture)
- [Developer Notes: Compiling Protocol Buffers](#developer-notes-compiling-protocol-buffers)
- [Approaches Tried](#approaches-tried)
    - [Spring WebFlux](#1-spring-webflux)
    - [net.devh gRPC Libraries](#2-netdevh-grpc-libraries)
    - [Custom Proxy Approach](#3-custom-proxy-approach)
    - [Armeria (Selected Solution)](#4-armeria-selected-solution)
- [Why Armeria Works Best](#why-armeria-works-best)
- [Why Unframed Requests Are Important](#why-unframed-requests-are-important)
- [Comparing different streaming technologies](#comparing-different-streaming-technologies)


## How it Works: gRPC-Web Streaming

1.  **Client Request:** The React frontend sends an HTTP request containing a gRPC message.
2.  **Server Handling:** The Armeria-based Java backend receives the request. Armeria natively supports both gRPC and gRPC-Web.
3.  **Streaming Response:** The server keeps the HTTP connection open, continuously pushing new stock data (as Protocol Buffer messages) over this connection.
4.  **gRPC-Web Translation:** Armeria handles the translation between standard gRPC and the browser-compatible gRPC-Web protocol. Key aspects include:
    * **Unframed Requests:** Essential for browser compatibility, allowing gRPC messages over standard HTTP as browsers don't handle native gRPC framing.
    * **Protocol Buffers (Protobuf):** `.proto` files define the data structure. They are compiled to Java (backend) and JavaScript (frontend) for type safety and consistency.

## Technology Choice: Armeria

While approaches using Spring WebFlux, `net.devh`, or custom proxies were considered, Armeria was selected for its superior gRPC-Web integration.

**Why Armeria:**

* **Native gRPC-Web:** Supports `enableUnframedRequests(true)` out-of-the-box.
* **Efficient Streaming:** Handles long-running streams effectively with configurable timeouts.
* **Seamless Integration:** Works well with Spring Boot and standard gRPC implementations.
* **Simplified Architecture:** Acts as a direct bridge, avoiding complex proxy layers.
* **Unified Server:** Serves gRPC/gRPC-Web, REST APIs, and static files from one instance.
* **Observability:** Provides built-in logging and monitoring.

## Architecture

```
┌─────────────────┐      ┌─────────────────────────────────────────┐
│                 │      │                                         │
│  React Frontend │      │             Java Backend                │
│                 │      │                                         │
│  ┌───────────┐  │      │  ┌───────────┐       ┌───────────────┐  │
│  │           │  │ gRPC │  │           │       │               │  │
│  │  Browser  │◄─┼──────┼──┤  Armeria  │───────►  Static Files │  │
│  │  Client   │  │HTTP  │  |  Server   │       │   Delivery    │  │
│  │           │  │      │  │           │       │               │  │
│  └───────────┘  │      │  └───┬───────┘       └───────────────┘  │
│        ▲        │      │      │                                  │
│        │        │      │      │                                  │
│  ┌─────┴─────┐  │      │  ┌───▼───────────┐                      │
│  │ AG Grid   │  │.     │  │               │                      │
│  │ (display) │  │      │  │  StockService │                      │
│  └───────────┘  │      │  │  (gRPC impl)  │                      │
│                 │      │  │               │                      │
└─────────────────┘      │  └───────────────┘                      │
                         │                                         │
                         └─────────────────────────────────────────┘

  1. Initial static files loaded via HTTP
  2. gRPC-Web Stream: Stock updates flow continuously
```


* **Frontend (React):** Displays data using AG Grid, communicates via gRPC-Web.
* **Backend (Java/Armeria):** Handles gRPC-Web requests, implements `StockService`, and serves static frontend files.
* **Data Flow:** Initial page load via HTTP, then continuous stock updates via a persistent gRPC-Web stream.

## Developer Notes: Compiling Protocol Buffers

Protocol Buffers (`.proto` files in `backend/src/main/proto`) must be compiled to JavaScript for the frontend.

**1. Setup (Run once):**
Install required Node.js development tools in your frontend project:
```bash
npm install --save-dev protoc-gen-js google-protobuf grpc-web
```

**2. Compilation Command::**
```json
"scripts": {
"proto": "protoc --proto_path=../backend/src/main/proto --plugin=protoc-gen-js=./node_modules/.bin/protoc-gen-js --js_out=import_style=commonjs:src --grpc-web_out=import_style=commonjs,mode=grpcwebtext:src stock.proto"
}
```

This command uses protoc to generate JS message classes (*_pb.js) and gRPC-Web client stubs (*_grpc_web_pb.js) from your .proto definitions. Ensure paths (--proto_path, output src) are correct for your project structure.

**3. Generate Files:**

Run the script from your frontend directory whenever the .proto files change:
```bash
npm run proto
```
This generates the necessary JavaScript files in the src directory, allowing your React code to interact with the gRPC backend

## Approaches Tried

### 1. Spring WebFlux
- Reactive programming model seemed promising for streaming
- Difficult to integrate with standard gRPC services
- Required complex translation between gRPC and reactive types

### 2. net.devh gRPC Libraries
- Good for standard gRPC services
- Default configuration failed to work with gRPC-Web browser clients
- Limited support for gRPC-Web (browser compatibility)
- Timeout issues with long-running streams
- Could not properly handle the protocol translation needed for browsers

### 3. Custom Proxy Approach
- Attempted to proxy gRPC traffic through standard HTTP endpoints
- Complex to implement and maintain
- Performance overhead due to translation layer

### 4. Armeria (Selected Solution)
- Native support for both gRPC and gRPC-Web
- Enables unframed requests critical for browser compatibility
- Configurable timeouts for long-running streams
- Integrated nicely with Spring Boot
- Built-in service documentation

## Why Armeria Works Best

Armeria was selected because:

1. It provides `enableUnframedRequests(true)` for proper gRPC-Web support
2. It allows fine-grained timeout configuration for streaming connections
3. It integrates seamlessly with existing gRPC service implementations
4. It has built-in logging and monitoring capabilities
5. It supports both gRPC and REST services in the same application

### Why Unframed Requests Are Important

Unframed requests are crucial for gRPC-Web compatibility because:

1. Browsers cannot directly handle standard gRPC framing protocol
2. Unframed requests allow the gRPC messages to be sent over standard HTTP/1.1 and HTTP/2
3. They enable proper translation between gRPC binary format and browser-compatible format
4. Without unframed requests, browsers would reject the binary framing used in standard gRPC
5. They allow streaming responses to work in browsers without WebSocket support

# Comparing different streaming technologies

Here is a comparison table for different streaming options to the web:

| Feature/Option                | Polling | SSE (Server-Sent Events) | Fetch with ReadableStream | gRPC-Web | WebSockets |
|-------------------------------|---------|--------------------------|---------------------------|----------|------------|
| **Real-time Updates**         | No      | Yes                      | Yes                       | Yes      | Yes        |
| **Bidirectional Communication**| No      | No                       | No                        | Yes      | Yes        |
| **Browser Support**           | Yes     | Yes                      | Yes                       | Yes      | Yes        |
| **Efficiency**                | Low     | Medium                   | High                      | High     | High       |
| **Complexity**                | Low     | Low                      | Medium                    | High     | Medium     |
| **Scalability**               | Low     | Medium                   | High                      | High     | High       |
| **Error Handling**            | Basic   | Basic                    | Advanced                  | Advanced | Advanced   |
| **Security**                  | Basic   | Basic                    | Advanced                  | Advanced | Advanced   |
| **Protocol**                  | HTTP    | HTTP                     | HTTP                      | HTTP/2   | TCP        |
| **Multiplexing**              | No      | No                       | Yes                       | Yes      | Yes        |
| **Type Safety**               | No      | No                       | No                        | Yes      | No         |
| **Code Generation**           | No      | No                       | No                        | Yes      | No         |

### Final Viewpoint
- **Polling**: Simple but inefficient and not suitable for real-time updates.
- **SSE**: Good for unidirectional real-time updates, but lacks bidirectional communication.
- **Fetch with ReadableStream**: Efficient and modern, but more complex to implement.
- **gRPC-Web**: Highly efficient, supports bidirectional communication, type safety, and advanced features, but more complex to set up.
- **WebSockets**: Efficient and supports bidirectional communication, but lacks some advanced features like type safety and built-in code generation.

Each option has its strengths and weaknesses, and the best choice depends on the specific requirements of your application.
