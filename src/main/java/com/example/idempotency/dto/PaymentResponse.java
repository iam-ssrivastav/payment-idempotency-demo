package com.example.idempotency.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment responses.
 * 
 * @author Shivam Srivastav
 */
public class PaymentResponse {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private boolean fromCache;

    public PaymentResponse() {
    }

    public PaymentResponse(Long id, BigDecimal amount, String currency, String status,
            String idempotencyKey, LocalDateTime createdAt, boolean fromCache) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.fromCache = fromCache;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }
}
