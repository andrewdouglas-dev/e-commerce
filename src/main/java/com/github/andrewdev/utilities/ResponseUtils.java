package com.github.andrewdev.utilities;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

public class ResponseUtils {
    private static final Logger logger = Logger.getLogger(ResponseUtils.class.getName());

    private ResponseUtils(){}

    public static void sendOK(HttpExchange exchange) {
        sendResponse(exchange, 200, null);
    }

    public static void sendBadRequest(HttpExchange exchange, String body) {
        sendResponse(exchange, 400, body);
    }

    public static void resourceNotFound(HttpExchange exchange) {
        sendResponse(exchange, 404, null);
    }

    public static void sendMethodNotAllowed(HttpExchange exchange) {
        sendResponse(exchange, 405, null);
    }

    public static void sendTooManyRequests(HttpExchange exchange) {
        sendResponse(exchange, 429, null);
    }

    public static void sendInternalServerError(HttpExchange exchange) {
        sendResponse(exchange, 500, null);
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String body) {
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        try {
            if (body == null) {
                exchange.sendResponseHeaders(statusCode, -1);

                return;
            }

            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(statusCode, body.getBytes(StandardCharsets.UTF_8).length);

                os.write(body.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.severe("Exception occured while writing to OS.");
            }

        } catch (Exception e) {
            logger.severe("Exception occured while sending a response to client.");
        }
    }
}
