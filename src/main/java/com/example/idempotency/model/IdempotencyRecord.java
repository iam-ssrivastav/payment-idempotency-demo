package com.example.idempotency.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model for storing idempotency records in Redis.
 * 
 * @author Shivam Srivastav
 */
public class IdempotencyRecord implements Serializable {
    private String key;
    private String requestHash;
    private Object response;
    private int statusCode;
    private LocalDateTime createdAt;

    public IdempotencyRecord() {
    }

    public IdempotencyRecord(String key, String requestHash, Object response, int statusCode, LocalDateTime createdAt) {
        this.key = key;
        this.requestHash = requestHash;
        this.response = response;
        this.statusCode = statusCode;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
