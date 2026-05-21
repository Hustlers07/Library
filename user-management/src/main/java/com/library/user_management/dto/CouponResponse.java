package com.library.user_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {

    private Long id;

    private String couponCode;

    private String description;

    private BigDecimal discountPercentage;

    private BigDecimal discountAmount;

    private BigDecimal minimumBookingAmount;

    private BigDecimal maximumDiscountAmount;

    private Integer usageLimit;

    private Integer usedCount;

    private LocalDateTime validFrom;

    private LocalDateTime validTill;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
