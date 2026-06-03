package com.library.user_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.library.user_management.entity.BookingStatus;
import com.library.user_management.entity.BookingType;
import com.library.user_management.entity.DurationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    private String userName;

    private Long roomId;

    private BookingType bookingType;

    private DurationType durationType;

    private Integer duration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String specialNotes;

    private String couponCode;

    private String planName;

    /**
     * List of seat IDs to book (required if bookingType is SEAT or FLOOR_AND_SEAT)
     * Leave empty or null for FLOOR booking
     */
    private List<Long> seatIds;
}
