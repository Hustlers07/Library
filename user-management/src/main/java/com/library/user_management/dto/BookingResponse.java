package com.library.user_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class BookingResponse {

    private Long id;

    private Long userId;

    private Long roomId;

    private BookingType bookingType;

    private DurationType durationType;

    private Integer duration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BookingStatus status;

    private BigDecimal basePrice;

    private BigDecimal totalPrice;

    private String specialNotes;

    private Boolean isRenewal;

    private Integer renewalCount;

    private Long parentBookingId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
