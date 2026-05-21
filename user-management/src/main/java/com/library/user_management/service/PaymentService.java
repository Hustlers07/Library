package com.library.user_management.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.library.user_management.dto.PaymentRequest;
import com.library.user_management.dto.PaymentResponse;
import com.library.user_management.entity.Booking;
import com.library.user_management.entity.Payment;
import com.library.user_management.entity.PaymentStatus;
import com.library.user_management.entity.User;
import com.library.user_management.repository.BookingRepository;
import com.library.user_management.repository.PaymentRepository;
import com.library.user_management.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    /**
     * Create a new payment for a booking
     */
    public PaymentResponse createPayment(Long userId, PaymentRequest paymentRequest) {
        log.info("Creating payment for user: {} and booking: {}", userId, paymentRequest.getBookingId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Booking does not belong to this user");
        }

        Payment payment = Payment.builder()
                .user(user)
                .booking(booking)
                .amount(booking.getTotalPrice())
                .discountAmount(booking.getCoupon() != null ? 
                    booking.getTotalPrice().multiply(booking.getCoupon().getDiscountPercentage().divide(BigDecimal.valueOf(100))) : 
                    BigDecimal.ZERO)
                .finalAmount(booking.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .transactionId(generateTransactionId())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentGateway(paymentRequest.getPaymentGateway())
                .notes(paymentRequest.getNotes())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    /**
     * Process payment and mark as completed
     */
    public PaymentResponse processPayment(Long paymentId) {
        log.info("Processing payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment cannot be processed in " + payment.getStatus() + " status");
        }

        // TODO: Integrate with actual payment gateway (Stripe, PayPal, etc.)
        // For now, marking as completed
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully: {}", paymentId);

        return mapToResponse(updatedPayment);
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return mapToResponse(payment);
    }

    /**
     * Get all payments for a user
     */
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        log.info("Fetching payments for user: {}", userId);
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get payments by status
     */
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Refund a payment
     */
    public PaymentResponse refundPayment(Long paymentId) {
        log.info("Refunding payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully: {}", paymentId);

        return mapToResponse(updatedPayment);
    }

    /**
     * Cancel a payment
     */
    public PaymentResponse cancelPayment(Long paymentId) {
        log.info("Cancelling payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalArgumentException("Cannot cancel payment in " + payment.getStatus() + " status");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment cancelled successfully: {}", paymentId);

        return mapToResponse(updatedPayment);
    }

    /**
     * Map Payment entity to response DTO
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .bookingId(payment.getBooking() != null ? payment.getBooking().getId() : null)
                .amount(payment.getAmount())
                .discountAmount(payment.getDiscountAmount())
                .finalAmount(payment.getFinalAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentGateway(payment.getPaymentGateway())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
