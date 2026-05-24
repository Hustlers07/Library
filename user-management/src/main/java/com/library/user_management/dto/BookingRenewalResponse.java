package com.library.user_management.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for booking renewal response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRenewalResponse {

    /**
     * Original booking ID
     */
    private Long originalBookingId;

    /**
     * List of newly created renewal bookings
     */
    private List<BookingResponse> renewalBookings;

    /**
     * Total number of renewals created
     */
    private Integer totalRenewalsCreated;

    /**
     * Renewal status message
     */
    private String message;
}
