package com.example.idempotency.service;

import com.example.idempotency.dto.PaymentRequest;
import com.example.idempotency.dto.PaymentResponse;
import com.example.idempotency.entity.Payment;
import com.example.idempotency.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for payment processing operations.
 * 
 * @author Shivam Srivastav
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, String idempotencyKey) {
        log.info("Creating payment for amount: {} {}, idempotency key: {}",
                request.getAmount(), request.getCurrency(), idempotencyKey);

        // Simulate payment processing delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus("COMPLETED");
        payment.setIdempotencyKey(idempotencyKey);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", saved.getId());

        return toResponse(saved, false);
    }

    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return toResponse(payment, false);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(p -> toResponse(p, false))
                .collect(Collectors.toList());
    }

    private PaymentResponse toResponse(Payment payment, boolean fromCache) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getIdempotencyKey(),
                payment.getCreatedAt(),
                fromCache);
    }
}
