package com.github.andrewdev.utilities;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

public class ResponseUtils {
    private static final Logger logger = Logger.getLogger(ResponseUtils.class.getName());

    private ResponseUtils(){}

    public static void sendOK(HttpExchange exchange) {
        sendResponseWithBody(exchange, 200, "{}");
    }

    public static void sendBadRequest(HttpExchange exchange, String body) {
        sendResponseWithBody(exchange, 400, body);
    }

    public static void resourceNotFound(HttpExchange exchange) {
        sendResponseWithNoBody(exchange, 404);
    }

    public static void sendMethodNotAllowed(HttpExchange exchange) {
        sendResponseWithNoBody(exchange, 405);
    }

    public static void sendTooManyRequests(HttpExchange exchange) {
        sendResponseWithNoBody(exchange, 429);
    }

    public static void sendInternalServerError(HttpExchange exchange) {
        sendResponseWithNoBody(exchange, 500);
    }

    private static void sendResponseWithNoBody(HttpExchange exchange, int statusCode) {
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        try {
            exchange.sendResponseHeaders(statusCode, -1);
        } catch (IOException ex) {
            logger.severe("Exception occured while sending a response header.");
        }
    }

    private static void sendResponseWithBody(HttpExchange exchange, int statusCode, String body) {
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, body.getBytes(StandardCharsets.UTF_8).length);

            os.write(body.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            logger.severe("Exception occured while sending a response to client.");
        }
    }
}
