package com.library.user_management.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.library.user_management.dto.BookingRequest;
import com.library.user_management.dto.BookingResponse;
import com.library.user_management.entity.Booking;
import com.library.user_management.entity.BookingItem;
import com.library.user_management.entity.BookingStatus;
import com.library.user_management.entity.Coupon;
import com.library.user_management.entity.Room;
import com.library.user_management.entity.Seat;
import com.library.user_management.entity.User;
import com.library.user_management.repository.BookingItemRepository;
import com.library.user_management.repository.BookingRepository;
import com.library.user_management.repository.CouponRepository;
import com.library.user_management.repository.RoomRepository;
import com.library.user_management.repository.SeatRepository;
import com.library.user_management.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final CouponRepository couponRepository;

    /**
     * Create a new booking
     * Supports booking complete floor or specific seats
     */
    public BookingResponse createBooking(Long userId, BookingRequest bookingRequest) {
        log.info("Creating booking for user: {} in room: {} with type: {}", 
                userId, bookingRequest.getRoomId(), bookingRequest.getBookingType());

        // Validate user and room
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Validate booking type and seats
        validateBookingType(bookingRequest);

        // Check for conflicting bookings
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                bookingRequest.getRoomId(),
                bookingRequest.getStartTime(),
                bookingRequest.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Room is already booked for the selected time period");
        }

        // Get seats if booking includes seats
        List<Seat> bookedSeats = new ArrayList<>();
        if (bookingRequest.getBookingType() == com.library.user_management.entity.BookingType.SEAT ||
            bookingRequest.getBookingType() == com.library.user_management.entity.BookingType.FLOOR_AND_SEAT) {
            
            if (bookingRequest.getSeatIds() == null || bookingRequest.getSeatIds().isEmpty()) {
                throw new IllegalArgumentException("Seat IDs are required for SEAT booking type");
            }

            bookedSeats = validateAndGetSeats(bookingRequest.getSeatIds(), room.getId());
        }

        // Calculate base price based on duration and booking type
        BigDecimal basePrice = calculatePrice(bookingRequest.getDurationType(), bookingRequest.getDuration(), 
                                              bookingRequest.getBookingType(), bookedSeats.size());

        // Apply coupon if provided
        Coupon coupon = null;
        if (bookingRequest.getCouponId() != null) {
            coupon = couponRepository.findById(bookingRequest.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
            
            if (!isCouponValid(coupon)) {
                throw new IllegalArgumentException("Coupon is not valid");
            }
        }

        BigDecimal totalPrice = calculateTotalPrice(basePrice, coupon);

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .bookingType(bookingRequest.getBookingType())
                .durationType(bookingRequest.getDurationType())
                .duration(bookingRequest.getDuration())
                .startTime(bookingRequest.getStartTime())
                .endTime(bookingRequest.getEndTime())
                .status(BookingStatus.PENDING)
                .basePrice(basePrice)
                .totalPrice(totalPrice)
                .coupon(coupon)
                .specialNotes(bookingRequest.getSpecialNotes())
                .bookingItems(new ArrayList<>())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {}", savedBooking.getId());

        // Create booking items for seats if applicable
        if (!bookedSeats.isEmpty()) {
            List<BookingItem> bookingItems = bookedSeats.stream()
                    .map(seat -> BookingItem.builder()
                            .booking(savedBooking)
                            .seat(seat)
                            .pricePerUnit(seat.getPricePerHour())
                            .build())
                    .collect(Collectors.toList());
            
            List<BookingItem> savedItems = bookingItemRepository.saveAll(bookingItems);
            savedBooking.setBookingItems(savedItems);
            log.info("Created {} booking items for booking ID: {}", savedItems.size(), savedBooking.getId());
        }

        return mapToResponse(savedBooking);
    }

    /**
     * Validate booking type and seat requirements
     */
    private void validateBookingType(BookingRequest bookingRequest) {
        com.library.user_management.entity.BookingType bookingType = bookingRequest.getBookingType();
        
        if (bookingType == null) {
            throw new IllegalArgumentException("Booking type is required");
        }

        if (bookingType == com.library.user_management.entity.BookingType.SEAT ||
            bookingType == com.library.user_management.entity.BookingType.FLOOR_AND_SEAT) {
            
            if (bookingRequest.getSeatIds() == null || bookingRequest.getSeatIds().isEmpty()) {
                throw new IllegalArgumentException("Seat IDs are required for " + bookingType + " booking");
            }
        }
    }

    /**
     * Validate seats exist and belong to the specified room
     */
    private List<Seat> validateAndGetSeats(List<Long> seatIds, Long roomId) {
        List<Seat> seats = new ArrayList<>();

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat with ID " + seatId + " not found"));

            if (!seat.getRoom().getId().equals(roomId)) {
                throw new IllegalArgumentException("Seat " + seatId + " does not belong to room " + roomId);
            }

            if (!seat.isActive()) {
                throw new IllegalArgumentException("Seat " + seatId + " is not active");
            }

            seats.add(seat);
        }

        return seats;
    }

    /**
     * Confirm a booking
     */
    public BookingResponse confirmBooking(Long bookingId) {
        log.info("Confirming booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking confirmed: {}", bookingId);

        return mapToResponse(updatedBooking);
    }

    /**
     * Cancel a booking
     */
    public BookingResponse cancelBooking(Long bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel completed bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking cancelled: {}", bookingId);

        return mapToResponse(updatedBooking);
    }

    /**
     * Get booking by ID
     */
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return mapToResponse(booking);
    }

    /**
     * Get all bookings for a user
     */
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get bookings by room
     */
    public List<BookingResponse> getBookingsByRoomId(Long roomId) {
        log.info("Fetching bookings for room: {}", roomId);
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get bookings by status
     */
    public List<BookingResponse> getBookingsByStatus(BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Calculate base price based on duration type, duration, booking type, and seat count
     */
    private BigDecimal calculatePrice(com.library.user_management.entity.DurationType durationType, 
                                     Integer duration,
                                     com.library.user_management.entity.BookingType bookingType,
                                     int seatCount) {
        // Base prices per unit
        BigDecimal hourlyRate = new BigDecimal("50");      // ₹50/hour for floor
        BigDecimal dailyRate = new BigDecimal("300");      // ₹300/day for floor
        BigDecimal weeklyRate = new BigDecimal("1500");    // ₹1500/week for floor
        BigDecimal monthlyRate = new BigDecimal("5000");   // ₹5000/month for floor

        BigDecimal basePrice;

        switch (durationType) {
            case HOURLY:
                basePrice = hourlyRate.multiply(new BigDecimal(duration));
                break;
            case DAILY:
                basePrice = dailyRate.multiply(new BigDecimal(duration));
                break;
            case WEEKLY:
                basePrice = weeklyRate.multiply(new BigDecimal(duration));
                break;
            case MONTHLY:
                basePrice = monthlyRate.multiply(new BigDecimal(duration));
                break;
            default:
                throw new IllegalArgumentException("Unknown duration type");
        }

        // If booking includes seats, multiply by number of seats
        if (bookingType == com.library.user_management.entity.BookingType.SEAT ||
            bookingType == com.library.user_management.entity.BookingType.FLOOR_AND_SEAT) {
            basePrice = basePrice.multiply(new BigDecimal(seatCount));
        }

        return basePrice;
    }

    /**
     * Calculate base price for backward compatibility (floor only)
     */
    private BigDecimal calculatePrice(com.library.user_management.entity.DurationType durationType, Integer duration) {
        return calculatePrice(durationType, duration, com.library.user_management.entity.BookingType.FLOOR, 0);
    }

    /**
     * Check if coupon is valid
     */
    private boolean isCouponValid(Coupon coupon) {
        if (coupon == null || !coupon.getIsActive()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTill())) {
            return false;
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return false;
        }

        return true;
    }

    /**
     * Calculate total price after applying coupon discount
     */
    private BigDecimal calculateTotalPrice(BigDecimal basePrice, Coupon coupon) {
        if (coupon == null) {
            return basePrice;
        }

        BigDecimal discount = BigDecimal.ZERO;

        // Apply percentage discount
        if (coupon.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            discount = basePrice.multiply(coupon.getDiscountPercentage()).divide(new BigDecimal("100"));
        }

        // Apply fixed discount
        if (coupon.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            discount = discount.add(coupon.getDiscountAmount());
        }

        // Apply maximum discount limit
        if (coupon.getMaximumDiscountAmount() != null && 
            discount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
            discount = coupon.getMaximumDiscountAmount();
        }

        BigDecimal totalPrice = basePrice.subtract(discount);
        return totalPrice.compareTo(BigDecimal.ZERO) > 0 ? totalPrice : BigDecimal.ZERO;
    }

    /**
     * Map Booking entity to response DTO
     */
    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .roomId(booking.getRoom().getId())
                .bookingType(booking.getBookingType())
                .durationType(booking.getDurationType())
                .duration(booking.getDuration())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .basePrice(booking.getBasePrice())
                .totalPrice(booking.getTotalPrice())
                .specialNotes(booking.getSpecialNotes())
                .isRenewal(booking.getIsRenewal())
                .renewalCount(booking.getRenewalCount())
                .parentBookingId(booking.getParentBooking() != null ? booking.getParentBooking().getId() : null)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    /**
     * Renew a booking after payment completion
     * Creates new booking(s) with same details but for the next period
     */
    public List<BookingResponse> renewBooking(Long bookingId, 
                                             com.library.user_management.dto.BookingRenewalRequest renewalRequest) {
        log.info("Renewing booking with ID: {} - renewal count: {}", bookingId, renewalRequest.getRenewalCount());

        Booking originalBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Validate that payment has been completed
        if (originalBooking.getPayment() == null) {
            throw new IllegalArgumentException("Booking must have a payment before renewal");
        }

        if (originalBooking.getPayment().getStatus() != com.library.user_management.entity.PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment must be completed before renewal. Current status: " + 
                    originalBooking.getPayment().getStatus());
        }

        // Validate renewal count
        Integer renewalCount = renewalRequest.getRenewalCount() != null ? renewalRequest.getRenewalCount() : 1;
        if (renewalCount <= 0 || renewalCount > 12) {
            throw new IllegalArgumentException("Renewal count must be between 1 and 12");
        }

        List<BookingResponse> renewedBookings = new ArrayList<>();
        LocalDateTime currentStartTime = renewalRequest.getNewStartTime() != null ? 
            renewalRequest.getNewStartTime() : originalBooking.getEndTime();

        // Get seats if booking type includes seats
        List<Seat> bookedSeats = new ArrayList<>();
        if (originalBooking.getBookingType() == com.library.user_management.entity.BookingType.SEAT ||
            originalBooking.getBookingType() == com.library.user_management.entity.BookingType.FLOOR_AND_SEAT) {
            
            bookedSeats = originalBooking.getBookingItems().stream()
                    .map(BookingItem::getSeat)
                    .collect(Collectors.toList());
        }

        // Get coupon for renewal if provided
        Coupon renewalCoupon = null;
        if (renewalRequest.getCouponId() != null) {
            renewalCoupon = couponRepository.findById(renewalRequest.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
            
            if (!isCouponValid(renewalCoupon)) {
                throw new IllegalArgumentException("Coupon is not valid for renewal");
            }
        }

        // Create renewal bookings
        for (int i = 0; i < renewalCount; i++) {
            LocalDateTime renewalStartTime = currentStartTime;
            LocalDateTime renewalEndTime = calculateEndTime(renewalStartTime, 
                    originalBooking.getDurationType(), 
                    originalBooking.getDuration());

            // Check for conflicts
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                    originalBooking.getRoom().getId(),
                    renewalStartTime,
                    renewalEndTime);

            if (!conflicts.isEmpty()) {
                log.warn("Conflict found for renewal {} at {}", i + 1, renewalStartTime);
                throw new IllegalArgumentException("Cannot create renewal " + (i + 1) + 
                    ": Room is already booked for this period");
            }

            // Calculate price
            BigDecimal basePrice = calculatePrice(originalBooking.getDurationType(), 
                    originalBooking.getDuration(),
                    originalBooking.getBookingType(), 
                    bookedSeats.size());

            BigDecimal totalPrice = calculateTotalPrice(basePrice, renewalCoupon);

            // Create renewal booking
            Booking renewalBooking = Booking.builder()
                    .user(originalBooking.getUser())
                    .room(originalBooking.getRoom())
                    .bookingType(originalBooking.getBookingType())
                    .durationType(originalBooking.getDurationType())
                    .duration(originalBooking.getDuration())
                    .startTime(renewalStartTime)
                    .endTime(renewalEndTime)
                    .status(BookingStatus.PENDING)
                    .basePrice(basePrice)
                    .totalPrice(totalPrice)
                    .coupon(renewalCoupon)
                    .specialNotes(renewalRequest.getRenewalNotes() != null ? 
                            renewalRequest.getRenewalNotes() : 
                            "Renewal of booking #" + originalBooking.getId())
                    .parentBooking(originalBooking)
                    .isRenewal(true)
                    .renewalCount(i + 1)
                    .bookingItems(new ArrayList<>())
                    .build();

            Booking savedRenewalBooking = bookingRepository.save(renewalBooking);
            log.info("Renewal booking {} created with ID: {}", i + 1, savedRenewalBooking.getId());

            // Create booking items for seats if applicable
            if (!bookedSeats.isEmpty()) {
                List<BookingItem> bookingItems = bookedSeats.stream()
                        .map(seat -> BookingItem.builder()
                                .booking(savedRenewalBooking)
                                .seat(seat)
                                .pricePerUnit(seat.getPricePerHour())
                                .build())
                        .collect(Collectors.toList());
                
                List<BookingItem> savedItems = bookingItemRepository.saveAll(bookingItems);
                savedRenewalBooking.setBookingItems(savedItems);
                log.info("Created {} booking items for renewal booking ID: {}", savedItems.size(), savedRenewalBooking.getId());
            }

            renewedBookings.add(mapToResponse(savedRenewalBooking));

            // Update start time for next renewal
            currentStartTime = renewalEndTime;
        }

        // Update original booking renewal count
        originalBooking.setRenewalCount(originalBooking.getRenewalCount() + renewalCount);
        bookingRepository.save(originalBooking);

        log.info("Booking {} renewed {} times", bookingId, renewalCount);
        return renewedBookings;
    }

    /**
     * Calculate end time based on start time, duration type, and duration
     */
    private LocalDateTime calculateEndTime(LocalDateTime startTime, 
                                          com.library.user_management.entity.DurationType durationType,
                                          Integer duration) {
        switch (durationType) {
            case HOURLY:
                return startTime.plusHours(duration);
            case DAILY:
                return startTime.plusDays(duration);
            case WEEKLY:
                return startTime.plusWeeks(duration);
            case MONTHLY:
                return startTime.plusMonths(duration);
            default:
                throw new IllegalArgumentException("Unknown duration type");
        }
    }

    /**
     * Get renewal history for a booking
     */
    public List<BookingResponse> getBookingRenewalHistory(Long bookingId) {
        log.info("Fetching renewal history for booking: {}", bookingId);

        Booking originalBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Find all renewals of this booking
        List<Booking> renewals = bookingRepository.findByParentBookingId(bookingId);
        
        return renewals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
