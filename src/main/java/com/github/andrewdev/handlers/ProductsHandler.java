package com.github.andrewdev.handlers;

import java.io.IOException;

import com.github.andrewdev.utilities.ResponseUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ProductsHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            ResponseUtils.sendMethodNotAllowed(exchange);

            return;
        }
    }
}
