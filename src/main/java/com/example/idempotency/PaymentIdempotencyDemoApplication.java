package com.example.idempotency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Payment Idempotency Demo.
 * Demonstrates idempotency pattern for payment processing.
 * 
 * @author Shivam Srivastav
 */
@SpringBootApplication
public class PaymentIdempotencyDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentIdempotencyDemoApplication.class, args);
    }

}
