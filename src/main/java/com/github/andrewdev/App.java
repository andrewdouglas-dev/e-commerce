package com.github.andrewdev;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.andrewdev.handlers.ProductsHandler;
import com.sun.net.httpserver.HttpServer;

public class App {
    public static void main( String[] args ) throws IOException
    {
        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/v1/products", new ProductsHandler());

        server.setExecutor(null);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
        }));
    }
}
