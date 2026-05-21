package com.library.user_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.library.user_management.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;

    private Long userId;

    private Long bookingId;

    private BigDecimal amount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    private PaymentStatus status;

    private String transactionId;

    private String paymentMethod;

    private String paymentGateway;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
