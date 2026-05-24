package com.library.user_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.library.user_management.dto.BookingRequest;
import com.library.user_management.dto.BookingResponse;
import com.library.user_management.dto.BookingRenewalRequest;
import com.library.user_management.dto.BookingRenewalResponse;
import com.library.user_management.entity.BookingStatus;
import com.library.user_management.entity.Role;
import com.library.user_management.security.JwtTokenProvider;
import com.library.user_management.service.BookingService;
import com.library.user_management.entity.User;
import com.library.user_management.service.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userService;

    /**
     * Create a new booking
     */
    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest bookingRequest,
            HttpServletRequest httpRequest) {
        log.info("Create booking request received");

        String token = extractToken(httpRequest);
        String username = jwtTokenProvider.extractUsername(token);

        BookingResponse response = bookingService.createBooking(username, bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Confirm a booking
     */
    @PutMapping("/{bookingId}/confirm")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long bookingId) {
        log.info("Confirm booking request for ID: {}", bookingId);

        BookingResponse response = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a booking
     */
    @PutMapping("/{bookingId}/cancel")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        log.info("Cancel booking request for ID: {}", bookingId);

        BookingResponse response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{bookingId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long bookingId) {
        log.info("Get booking request for ID: {}", bookingId);

        BookingResponse response = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all bookings for the current user
     */
    @GetMapping("/my-bookings")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(HttpServletRequest httpRequest) {
        log.info("Get my bookings request");

        String token = extractToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<BookingResponse> response = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all bookings for a room (Admin/Librarian only)
     */
    @GetMapping("/room/{roomId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getBookingsByRoom(@PathVariable Long roomId) {
        log.info("Get bookings for room ID: {}", roomId);

        List<BookingResponse> response = bookingService.getBookingsByRoomId(roomId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get bookings by status (Admin only)
     */
    @GetMapping("/status/{status}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(@PathVariable BookingStatus status) {
        log.info("Get bookings by status: {}", status);

        List<BookingResponse> response = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Renew a booking after payment completion
     */
    @PostMapping("/{bookingId}/renew")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<BookingRenewalResponse> renewBooking(
            @PathVariable Long bookingId,
            @RequestBody BookingRenewalRequest renewalRequest,
            HttpServletRequest httpRequest) {
        log.info("Renew booking request for ID: {}", bookingId);

        String token = extractToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // Verify booking belongs to user
        BookingResponse booking = bookingService.getBookingById(bookingId);
        if (!booking.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<BookingResponse> renewalBookings = bookingService.renewBooking(bookingId, renewalRequest);

        BookingRenewalResponse response = BookingRenewalResponse.builder()
                .originalBookingId(bookingId)
                .renewalBookings(renewalBookings)
                .totalRenewalsCreated(renewalBookings.size())
                .message("Successfully created " + renewalBookings.size() + " renewal booking(s)")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get renewal history for a booking
     */
    @GetMapping("/{bookingId}/renewal-history")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getBookingRenewalHistory(
            @PathVariable Long bookingId,
            HttpServletRequest httpRequest) {
        log.info("Get renewal history for booking ID: {}", bookingId);

        String token = extractToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // Verify booking belongs to user or user is admin
        BookingResponse booking = bookingService.getBookingById(bookingId);
        if (!booking.getUserId().equals(userId) && !isAdmin(httpRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<BookingResponse> renewalHistory = bookingService.getBookingRenewalHistory(bookingId);
        return ResponseEntity.ok(renewalHistory);
    }

    /**
     * Extract JWT token from request header
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin(HttpServletRequest request) {
        String token = extractToken(request);
        boolean isRoleAdmin = false;

        if (token != null) {
            String username = jwtTokenProvider.extractUsername(token);

            try {

                User user = userService.findUserByUsername(username);
                isRoleAdmin = user.getRole() == Role.ROLE_ADMIN;
            } catch (Exception ex) {
                isRoleAdmin = false;
            }
        }
        return isRoleAdmin;
    }
}
