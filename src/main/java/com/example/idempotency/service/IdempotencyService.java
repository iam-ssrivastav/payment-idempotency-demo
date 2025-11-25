package com.example.idempotency.service;

import com.example.idempotency.model.IdempotencyRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    private static final String KEY_PREFIX = "idempotency:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public IdempotencyService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Check if idempotency key exists
     */
    public boolean exists(String key) {
        String redisKey = KEY_PREFIX + key;
        Boolean exists = redisTemplate.hasKey(redisKey);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Get cached response for idempotency key
     */
    public IdempotencyRecord get(String key) {
        String redisKey = KEY_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(redisKey);

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.convertValue(value, IdempotencyRecord.class);
        } catch (Exception e) {
            log.error("Error deserializing idempotency record", e);
            return null;
        }
    }

    /**
     * Store idempotency record
     */
    public void store(String key, Object response, int statusCode, long ttlSeconds) {
        String redisKey = KEY_PREFIX + key;

        IdempotencyRecord record = new IdempotencyRecord(
                key,
                null, // requestHash can be added for extra validation
                response,
                statusCode,
                LocalDateTime.now());

        redisTemplate.opsForValue().set(redisKey, record, ttlSeconds, TimeUnit.SECONDS);
        log.info("Stored idempotency record for key: {}", key);
    }

    /**
     * Delete idempotency record (for testing/cleanup)
     */
    public void delete(String key) {
        String redisKey = KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
        log.info("Deleted idempotency record for key: {}", key);
    }
}
