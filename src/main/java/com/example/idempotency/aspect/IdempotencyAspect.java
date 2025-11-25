package com.example.idempotency.aspect;

import com.example.idempotency.annotation.Idempotent;
import com.example.idempotency.exception.IdempotencyKeyMissingException;
import com.example.idempotency.model.IdempotencyRecord;
import com.example.idempotency.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP Aspect that intercepts methods annotated with @Idempotent.
 * Checks Redis for duplicate requests and returns cached responses.
 * 
 * @author Shivam Srivastav
 */
@Aspect
@Component
public class IdempotencyAspect {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyAspect.class);
    private final IdempotencyService idempotencyService;

    public IdempotencyAspect(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // Get idempotency key from request header
        String idempotencyKey = getIdempotencyKey(idempotent.keyHeader());

        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new IdempotencyKeyMissingException(
                    "Idempotency-Key header is required for this endpoint");
        }

        log.info("Processing request with idempotency key: {}", idempotencyKey);

        // Check if we've seen this key before
        if (idempotencyService.exists(idempotencyKey)) {
            log.info("Idempotency key already exists, returning cached response");
            IdempotencyRecord record = idempotencyService.get(idempotencyKey);

            if (record != null) {
                // Return cached response
                return ResponseEntity
                        .status(record.getStatusCode())
                        .body(record.getResponse());
            }
        }

        // Execute the method (first time seeing this key)
        log.info("First time seeing this key, executing method");
        Object result = joinPoint.proceed();

        // Cache the response
        if (result instanceof ResponseEntity<?> responseEntity) {
            idempotencyService.store(
                    idempotencyKey,
                    responseEntity.getBody(),
                    responseEntity.getStatusCode().value(),
                    idempotent.ttlSeconds());
        }

        return result;
    }

    private String getIdempotencyKey(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(headerName);
        }

        return null;
    }
}
