package com.github.andrewdev.handlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrewdev.data.service.EmployeeService;
import com.github.andrewdev.utilities.ResponseUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import redis.clients.jedis.RedisClient;

public class AuthenticationHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(AuthenticationHandler.class.getName());
    private final RedisClient redis;
    private final EmployeeService employeeService;

    public AuthenticationHandler() {
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

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
