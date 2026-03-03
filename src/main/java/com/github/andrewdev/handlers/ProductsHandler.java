package com.github.andrewdev.handlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.utilities.RateLimiter;
import com.github.andrewdev.utilities.ResponseUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import redis.clients.jedis.RedisClient;

public class ProductsHandler implements HttpHandler{
    private static final Logger logger = Logger.getLogger(ProductsHandler.class.getName());
    private final RedisClient redis;

    public ProductsHandler() {
        RedisClient client = null;

        try {
            client = RedisClient.create("redis://ecommerce-redis:6379");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Redis - proceeding without rate-limiting.", e);
        }

        this.redis = client;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            ResponseUtils.sendMethodNotAllowed(exchange);

            return;
        }

        if (RateLimiter.isExceeded(redis, exchange.getRemoteAddress().getAddress().getHostAddress())) {
            ResponseUtils.sendTooManyRequests(exchange);
        }

        ResponseUtils.sendOK(exchange);
    }
}
