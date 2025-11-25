package com.example.idempotency.controller;

import com.example.idempotency.annotation.Idempotent;
import com.example.idempotency.dto.PaymentRequest;
import com.example.idempotency.dto.PaymentResponse;
import com.example.idempotency.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for payment operations.
 * Demonstrates idempotent payment processing.
 * 
 * @author Shivam Srivastav
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Create payment - IDEMPOTENT
     * Requires "Idempotency-Key" header
     */
    @PostMapping
    @Idempotent(keyHeader = "Idempotency-Key", ttlSeconds = 86400)
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {

        String idempotencyKey = httpRequest.getHeader("Idempotency-Key");
        PaymentResponse response = paymentService.createPayment(request, idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPayment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
