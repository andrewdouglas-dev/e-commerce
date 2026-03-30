package com.github.andrewdev.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.dao.ProductDAO;
import com.github.andrewdev.data.impl.ProductDAOImpl;
import com.github.andrewdev.models.Product;
import com.github.andrewdev.models.dto.ProductRequest;
import com.github.andrewdev.utilities.PathParser;
import com.github.andrewdev.utilities.RateLimiter;
import com.github.andrewdev.utilities.ResponseUtils;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import redis.clients.jedis.RedisClient;

enum RequestType {
    GET,
    POST,
    PUT,
    DELETE
}

public class ProductsHandler implements HttpHandler{
    private static final Logger logger = Logger.getLogger(ProductsHandler.class.getName());
    private static final Gson gson = new Gson();
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
        if (redis == null) {
            logger.info("Redis is null");
            ResponseUtils.sendInternalServerError(exchange);
            return;
        }

        if (RateLimiter.isExceeded(redis, exchange.getRemoteAddress().getAddress().getHostAddress())) {
            ResponseUtils.sendTooManyRequests(exchange);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGET(exchange, path);
                break;
            case "POST":
                handlePUTAndPOST(exchange, path, RequestType.POST);
                break;
            case "PUT":
                handlePUTAndPOST(exchange, path, RequestType.PUT);
                break;
            case "DELETE":
                handleDELETE(exchange, path);
                break;
            default:
                ResponseUtils.sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void handleGET(HttpExchange exchange, String path) {
        ProductDAO getProduct = new ProductDAOImpl();

        if (PathParser.isSpecificResourceRequested(path)) {
            try {
                Optional<Product> product = getProduct.findById(PathParser.extractResourceId(path));

                ResponseUtils.sendOK(exchange, gson.toJson(product));
            } catch (Exception e) {
                ResponseUtils.sendInternalServerError(exchange);
            }

            return;
        }

        try {
            List<Product> products = getProduct.findAll();

            ResponseUtils.sendOK(exchange, gson.toJson(products));
        } catch (Exception e) {
            ResponseUtils.sendInternalServerError(exchange);
        }
    }

    private void handlePUTAndPOST(HttpExchange exchange, String path, RequestType requestType) {
        if (PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendResourceNotFound(exchange);

            return;
        }

        ProductDAO modifyProduct = new ProductDAOImpl();

        try (InputStream is = exchange.getRequestBody();
            Reader reader = new InputStreamReader(is)){
            ProductRequest productDTO = gson.fromJson(reader, ProductRequest.class);
            Product productToUpdate = convertToProduct(productDTO);

            if (requestType == RequestType.POST) {
                modifyProduct.add(productToUpdate);
                ResponseUtils.sendCreated(exchange, gson.toJson(modifyProduct.add(productToUpdate)));
            } else {
                modifyProduct.update(productToUpdate);
                ResponseUtils.sendOK(exchange, gson.toJson(productToUpdate));
            }
        } catch (Exception e) {
            ResponseUtils.sendInternalServerError(exchange);
        }
    }

    private void handleDELETE(HttpExchange exchange, String path) {
        if (!PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendBadRequest(exchange, path);

            return;
        }

        ProductDAO deleteProduct = new ProductDAOImpl();

        try {
            Long id = PathParser.extractResourceId(path);

            deleteProduct.delete(id);

            ResponseUtils.sendOK(exchange, null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing deletion request.", e);

            ResponseUtils.sendInternalServerError(exchange);
        }
    }

    //WILL BE MOVED TO SEPERATE UTIL CLASS
    private Product convertToProduct(ProductRequest request) {
        return new Product(
            request.getId(),
            request.getName(),
            request.getPrice(),
            request.getQuantity()
        );
    }
}
