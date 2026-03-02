package com.github.andrewdev.utilities;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

public class ResponseUtils {
    private static final Logger logger = Logger.getLogger(ResponseUtils.class.getName());

    private ResponseUtils(){}

    public static void sendMethodNotAllowed(HttpExchange exchange) {
        sendResponse(exchange, 405, null);
    } 

    private static void sendResponse(HttpExchange exchange, int statusCode, String body) {
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        try {
            if (body == null) {
                exchange.sendResponseHeaders(statusCode, -1);

                return;
            }

            exchange.sendResponseHeaders(statusCode, body.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();

            os.write(body.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            logger.severe("Exception occured while sending a response to client.");
        }
    }
}
