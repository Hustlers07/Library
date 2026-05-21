package com.library.user_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.library.user_management.entity.PlanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {

    private String planName;

    private PlanType planType;

    private String description;

    private BigDecimal price;

    private Integer validityDays;

    private Integer hourlyLimit;

    private Integer dailyLimit;

    private Integer monthlyLimit;

    private Boolean floorAccessAllowed;

    private Boolean seatAccessAllowed;

    private BigDecimal discountPercentage;
}
