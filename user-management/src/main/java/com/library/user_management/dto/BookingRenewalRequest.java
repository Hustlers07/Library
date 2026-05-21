package com.library.user_management.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for renewing a booking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRenewalRequest {

    /**
     * The booking ID to renew
     */
    private Long bookingId;

    /**
     * Optional: New start time for renewal (defaults to after current booking ends)
     */
    private LocalDateTime newStartTime;

    /**
     * Optional: Number of renewals to create (default: 1)
     * Example: renewalCount = 3 will create 3 consecutive renewals
     */
    private Integer renewalCount = 1;

    /**
     * Optional: Apply same coupon to renewal (if provided)
     */
    private Long couponId;

    /**
     * Optional: Special notes for the renewal
     */
    private String renewalNotes;
}
