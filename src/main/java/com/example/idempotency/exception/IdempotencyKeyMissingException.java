package com.example.idempotency.exception;

/**
 * Exception thrown when idempotency key is missing from request.
 * 
 * @author Shivam Srivastav
 */
public class IdempotencyKeyMissingException extends RuntimeException {
    public IdempotencyKeyMissingException(String message) {
        super(message);
    }
}
