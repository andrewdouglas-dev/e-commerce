package com.github.andrewdev.utilities;

import java.time.Instant;
import java.util.Optional;
import java.util.logging.Logger;

import redis.clients.jedis.RedisClient;

public class RateLimiter {
    private static final Logger logger = Logger.getLogger(RateLimiter.class.getName());

    private RateLimiter() {}

    public static boolean isExceeded(RedisClient redis, String clientID) {
        String tokenCountKey = String.format("ratelimit:%s:count", clientID);
        String lastFilledKey = String.format("ratelimit:%s:lastRefill", clientID);

        long currentTimeStamp = Instant.now().getEpochSecond();

        Optional<String> tokenCount = Optional.ofNullable(redis.get(tokenCountKey));
        Optional<String> lastFilled = Optional.ofNullable(redis.get(lastFilledKey));

        if (tokenCount.isEmpty() || lastFilled.isEmpty()) {
            redis.setex(tokenCountKey, 86400, String.valueOf(Integer.parseInt(System.getenv("MAX_BUCKET_SIZE"))-1));
            redis.setex(lastFilledKey, 86400, String.valueOf(currentTimeStamp));

            return false;
        }

        long secondsElapsed = currentTimeStamp-Long.parseLong(lastFilled.get());
        int currentTokens = Integer.parseInt(tokenCount.get());
        int tokensToAdd = (int) Math.floor(secondsElapsed*Double.parseDouble(System.getenv("BUCKET_REPLISHMENT")));

        // logger.info("Current Tokens: " + currentTokens + " tokens to add: " + tokensToAdd);

        int newTokens = currentTokens + tokensToAdd;

        newTokens = Math.min(Integer.parseInt(System.getenv("MAX_BUCKET_SIZE")), newTokens);

        boolean empty = newTokens == 0;

        if (!empty) {
            newTokens--;
        }

        redis.setex(tokenCountKey, 86400, String.valueOf(newTokens));
        redis.setex(lastFilledKey, 86400, String.valueOf(currentTimeStamp));

        // logger.info("After Current Tokens: " + newTokens);

        return empty;
    }
}
