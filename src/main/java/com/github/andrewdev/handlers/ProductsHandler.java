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
import com.google.gson.JsonSyntaxException;
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
            client = RedisClient.create(System.getenv("REDIS_URL"));
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
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendBadRequest(exchange, e.getMessage());
        } catch (Exception e) {
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
        } else {
            ResponseUtils.sendOK(exchange, gson.toJson(productService.findAll()));
        }
    }

    private void handlePOST(HttpExchange exchange, String path) throws IOException, Exception {
        if (PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendResourceNotFound(exchange);
            return;
        }

        ProductRequest productRequest = parseRequestBody(exchange);

        ResponseUtils.sendCreated(exchange, gson.toJson(productService.create(ProductMapper.convertToNewProduct(productRequest))));
    }

    private void handlePUT(HttpExchange exchange, String path) throws Exception {
        if (!PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendBadRequest(exchange, "ID is required for update.");
            return;
        }

        Long id = PathParser.extractResourceId(path);

        ProductRequest productRequest = parseRequestBody(exchange);

        ResponseUtils.sendOK(exchange, gson.toJson(productService.update(productRequest, id).toString()));
    }

    private void handleDELETE(HttpExchange exchange, String path) {
        if (!PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendBadRequest(exchange, "ID is required for delete.");
            return;
        }

        productService.delete(PathParser.extractResourceId(path));

        ResponseUtils.sendOK(exchange, null);      
    }

    private ProductRequest parseRequestBody(HttpExchange exchange) throws Exception {
        try (InputStream is = exchange.getRequestBody();
            Reader reader = new InputStreamReader(is)){

            ProductRequest productDTO = gson.fromJson(reader, ProductRequest.class);
            productDTO.validate();

            return productDTO;
        } catch (JsonSyntaxException e) {
            if (e.getMessage().contains("BigDecimal")) {
                throw new IllegalArgumentException("Provided product price must be a number.");
            }
            if (e.getMessage().contains("NumberFormatException")) {
                throw new IllegalArgumentException("Provided product quantity must be a number.");
            }
            throw new IllegalArgumentException("Invalid request body syntax.");
        } catch (Exception e) {
            throw e;
        }
    }
}