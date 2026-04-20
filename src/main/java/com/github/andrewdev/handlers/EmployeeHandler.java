package com.github.andrewdev.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.service.EmployeeService;
import com.github.andrewdev.dto.EmployeeRequest;
import com.github.andrewdev.mapper.EmployeeMapper;
import com.github.andrewdev.utilities.PathParser;
import com.github.andrewdev.utilities.RateLimiter;
import com.github.andrewdev.utilities.ResponseUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import redis.clients.jedis.RedisClient;

public class EmployeeHandler implements HttpHandler{
    private static final Logger logger = Logger.getLogger(EmployeeHandler.class.getName());
    private static final Gson gson = new Gson();
    private final RedisClient redis;
    private final EmployeeService employeeService;

    public EmployeeHandler() {
        RedisClient client = null;

        try {
            client = RedisClient.create(System.getenv("REDIS_URL"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Redis - proceeding without rate-limiting.", e);
        }

        redis = client;
        employeeService = new EmployeeService();
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
                case "POST" -> handlePOST(exchange, path);
                case "GET" -> handleGet(exchange, path);
                default -> ResponseUtils.sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            ResponseUtils.sendInternalServerError(exchange);
        }
    }

    private void handlePOST(HttpExchange exchange, String path) throws IOException, Exception {
        if (PathParser.isSpecificResourceRequested(path)) {
            ResponseUtils.sendResourceNotFound(exchange);
            return;
        }

        EmployeeRequest employeeRequest = parseRequestBody(exchange);

        ResponseUtils.sendCreated(exchange, gson.toJson(employeeService.createEmployee(EmployeeMapper.convertToNewEmployee(employeeRequest), exchange.getRequestHeaders().get("Authorization").get(0))));
    }

    private void handleGet(HttpExchange exchange, String path) {
        if (PathParser.isSpecificResourceRequested(path)) {

        } else {
            ResponseUtils.sendOK(exchange, gson.toJson(employeeService.findAll()));
        }
    }

    private EmployeeRequest parseRequestBody(HttpExchange exchange) throws Exception {
        try (InputStream is = exchange.getRequestBody();
            Reader reader = new InputStreamReader(is);) {
            EmployeeRequest employeeDTO = gson.fromJson(reader, EmployeeRequest.class);
            employeeDTO.validate();

            return employeeDTO;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid request body syntax.");
        } catch (Exception e) {
            throw e;
        }
    }
}
