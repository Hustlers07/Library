package com.library.user_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.library.user_management.dto.PaymentRequest;
import com.library.user_management.dto.PaymentResponse;
import com.library.user_management.entity.PaymentStatus;
import com.library.user_management.security.JwtTokenProvider;
import com.library.user_management.service.PaymentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new payment
     */
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest httpRequest) {
        log.info("Create payment request received");

        // String token = extractToken(httpRequest);
        // Long userId = jwtTokenProvider.getUserIdFromToken(token);

        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Process a payment
     */
    @PutMapping("/{paymentId}/process")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long paymentId) {
        log.info("Process payment request for ID: {}", paymentId);

        PaymentResponse response = paymentService.processPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Refund a payment (Admin only)
     */
    @PutMapping("/{paymentId}/refund")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long paymentId) {
        log.info("Refund payment request for ID: {}", paymentId);

        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a payment
     */
    @PutMapping("/{paymentId}/cancel")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long paymentId) {
        log.info("Cancel payment request for ID: {}", paymentId);

        PaymentResponse response = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        log.info("Get payment request for ID: {}", paymentId);

        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for the current user
     */
    @GetMapping("/my-payments")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(HttpServletRequest httpRequest) {
        log.info("Get my payments request");

        String token = extractToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<PaymentResponse> response = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payments by status (Admin only)
     */
    @GetMapping("/status/{status}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        log.info("Get payments by status: {}", status);

        List<PaymentResponse> response = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Extract JWT token from request header
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
