package com.example.idempotency.exception;

public class IdempotencyKeyMissingException extends RuntimeException {
    public IdempotencyKeyMissingException(String message) {
        super(message);
    }
}
