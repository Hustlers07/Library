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
public class CouponRequest {

    private String couponCode;

    private String description;

    private BigDecimal discountPercentage;

    private BigDecimal discountAmount;

    private BigDecimal minimumBookingAmount;

    private BigDecimal maximumDiscountAmount;

    private Integer usageLimit;

    private LocalDateTime validFrom;

    private LocalDateTime validTill;
}
