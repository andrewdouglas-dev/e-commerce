package com.github.andrewdev.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.service.ProductService;
import com.github.andrewdev.dto.ProductRequest;
import com.github.andrewdev.mapper.ProductMapper;
import com.github.andrewdev.models.Product;
import com.github.andrewdev.utilities.PathParser;
import com.github.andrewdev.utilities.RateLimiter;
import com.github.andrewdev.utilities.ResponseUtils;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import redis.clients.jedis.RedisClient;

public class ProductsHandler implements HttpHandler{
    private static final Logger logger = Logger.getLogger(ProductsHandler.class.getName());
    private static final Gson gson = new Gson();
    private final RedisClient redis;
    private final ProductService productService;

    public ProductsHandler() {
        RedisClient client = null;

        try {
            client = RedisClient.create("redis://ecommerce-redis:6379");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Redis - proceeding without rate-limiting.", e);
        }

        this.redis = client;
        this.productService = new ProductService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (redis == null) {
            logger.info("Redis was not successfully instantiated.");
            ResponseUtils.sendInternalServerError(exchange);
            return;
        }

        if (RateLimiter.isExceeded(redis, exchange.getRemoteAddress().getAddress().getHostAddress())) {
            ResponseUtils.sendTooManyRequests(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> handleGET(exchange, path);
                case "POST"-> handlePOST(exchange, path);
                case "PUT" -> handlePUT(exchange, path);
                case "DELETE" -> handleDELETE(exchange, path);
                default -> ResponseUtils.sendMethodNotAllowed(exchange);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in Product Handler switch statement.", e);

            ResponseUtils.sendInternalServerError(exchange);
        }
    }

    private void handleGET(HttpExchange exchange, String path) {
        if (PathParser.isSpecificResourceRequested(path)) {
            Optional<Product> result = productService.findById(PathParser.extractResourceId(path));

            if (result.isEmpty()) {
                ResponseUtils.sendResourceNotFound(exchange);
                return;
            }

            ResponseUtils.sendOK(exchange, gson.toJson(result.get()));
            return;
        }

        ResponseUtils.sendOK(exchange, gson.toJson(productService.findAll()));
    }

    private void handlePOST(HttpExchange exchange, String path) throws IOException {
        if (PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendResourceNotFound(exchange);
            return;
        }

        Product product = parseRequestBody(exchange);

        if (product == null) {
            ResponseUtils.sendInternalServerError(exchange);
        }

        ResponseUtils.sendCreated(exchange, gson.toJson(productService.create(product)));
    }

    private void handlePUT(HttpExchange exchange, String path) throws IOException {
        if (!PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendBadRequest(exchange, "ID is required for update.");
            return;
        }

        Long id = PathParser.extractResourceId(path);

        if (productService.findById(id).isEmpty()) {
            ResponseUtils.sendResourceNotFound(exchange);
            return;
        }

        Product product = parseRequestBody(exchange);

        if (product == null) {
            ResponseUtils.sendBadRequest(exchange, "Invalid request body.");
            return;
        }

        product.setId(id);
        productService.update(product);

        ResponseUtils.sendOK(exchange, gson.toJson(product));
    }

    private void handleDELETE(HttpExchange exchange, String path) {
        if (!PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendBadRequest(exchange, "ID is required for delete.");
            return;
        }

        Long id = PathParser.extractResourceId(path);

        if (productService.findById(id).isEmpty()) {
            ResponseUtils.sendResourceNotFound(exchange);
            return;
        }

        productService.delete(id);

        ResponseUtils.sendOK(exchange, null);
    }

    private Product parseRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
            Reader reader = new InputStreamReader(is)){

            ProductRequest productDTO = gson.fromJson(reader, ProductRequest.class);

            return ProductMapper.convertToProduct(productDTO);
        }
    }
}