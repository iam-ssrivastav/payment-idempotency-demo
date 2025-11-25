# Payment Idempotency Demo

This project demonstrates **Idempotency** for payment processing using Spring Boot and Redis. It prevents duplicate payments even when clients retry the same request multiple times due to network failures or timeouts.

## Architecture

```mermaid
flowchart LR
    A[Client] -->|POST with Idempotency-Key| B[PaymentController]
    B -->|@Idempotent| C[IdempotencyAspect]
    C -->|Check Key| D[IdempotencyService]
    D <-->|Get/Set| E[(Redis)]
    C -->|Key Exists| F[Return Cached Response]
    C -->|New Key| G[Execute Payment]
    G -->|Save| H[(H2 Database)]
    G -->|Cache Response| E
    F -->|200 OK| A
    G -->|201 Created| A
    
    style E fill:#99ff99,stroke:#333,stroke-width:2px
    style H fill:#99ccff,stroke:#333,stroke-width:2px
```

## Key Concepts

### Idempotency
Making the same API call multiple times produces the same result as making it once. Critical for:
- **Payment Processing**: Prevent duplicate charges
- **Order Creation**: Avoid duplicate orders
- **Account Operations**: Ensure consistency

### How It Works
1. Client sends request with `Idempotency-Key` header
2. System checks if key exists in Redis
3. **If exists**: Return cached response (no duplicate)
4. **If new**: Execute operation, cache response

## Prerequisites
- Java 17+
- Docker (for Redis)
- Maven

## Getting Started

1. **Start Redis**:
   ```bash
   docker-compose up -d
   ```

2. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```
   Application starts on port **8083**.

3. **Run Verification**:
   ```bash
   chmod +x verify.sh
   ./verify.sh
   ```

## API Endpoints

### Create Payment (Idempotent)
```bash
curl -X POST http://localhost:8083/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: payment-123" \
  -d '{"amount": 100.50, "currency": "USD"}'
```

**Response** (201 Created):
```json
{
  "id": 1,
  "amount": 100.50,
  "currency": "USD",
  "status": "COMPLETED",
  "idempotencyKey": "payment-123",
  "createdAt": "2025-11-25T23:29:49"
}
```

### Retry with Same Key
```bash
curl -X POST http://localhost:8083/api/payments \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: payment-123" \
  -d '{"amount": 100.50, "currency": "USD"}'
```

**Response** (200 OK - Cached):
```json
{
  "id": 1,
  "amount": 100.50,
  "currency": "USD",
  "status": "COMPLETED",
  "idempotencyKey": "payment-123",
  "createdAt": "2025-11-25T23:29:49"
}
```
**Note**: Same payment ID returned, no duplicate created!

## Verification Results

```
Test 1: First payment → Created ID 1 ✅
Test 2: Same key → Returned ID 1 (cached, no duplicate) ✅
Test 3: Different key → Created ID 2 ✅
Test 4: No key → 400 Bad Request ✅
Test 5: Total payments → 2 ✅
```

## Implementation Details

### Custom Annotation
```java
@Idempotent(keyHeader = "Idempotency-Key", ttlSeconds = 86400)
public ResponseEntity<PaymentResponse> createPayment(...)
```

### AOP Aspect
Intercepts `@Idempotent` methods:
1. Extract idempotency key from header
2. Check Redis cache
3. Return cached response or execute method
4. Cache new responses

### Redis Storage
- **Key**: `idempotency:{key}`
- **Value**: Cached response + status code
- **TTL**: 24 hours (configurable)

## Production Considerations

1. **Key Generation**: Use UUID or client-generated unique IDs
2. **TTL**: Balance between deduplication window and storage
3. **Request Validation**: Hash request body for extra safety
4. **Monitoring**: Track cache hit rate
5. **Distributed Systems**: Redis cluster for high availability
