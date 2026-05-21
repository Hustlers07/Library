package com.library.user_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.library.user_management.dto.CouponRequest;
import com.library.user_management.dto.CouponResponse;
import com.library.user_management.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user-management/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * Create a new coupon (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest couponRequest) {
        log.info("Create coupon request received");

        CouponResponse response = couponService.createCoupon(couponRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update a coupon (Admin only)
     */
    @PutMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long couponId,
            @RequestBody CouponRequest couponRequest) {
        log.info("Update coupon request for ID: {}", couponId);

        CouponResponse response = couponService.updateCoupon(couponId, couponRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get coupon by ID
     */
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long couponId) {
        log.info("Get coupon request for ID: {}", couponId);

        CouponResponse response = couponService.getCouponById(couponId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get coupon by code
     */
    @GetMapping("/code/{couponCode}")
    public ResponseEntity<CouponResponse> getCouponByCode(@PathVariable String couponCode) {
        log.info("Get coupon request for code: {}", couponCode);

        CouponResponse response = couponService.getCouponByCode(couponCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all coupons
     */
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        log.info("Get all coupons request");

        List<CouponResponse> response = couponService.getAllCoupons();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active coupons
     */
    @GetMapping("/active")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        log.info("Get active coupons request");

        List<CouponResponse> response = couponService.getActiveCoupons();
        return ResponseEntity.ok(response);
    }

    /**
     * Apply a coupon code (increments usage count)
     */
    @PutMapping("/apply/{couponCode}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> applyCoupon(@PathVariable String couponCode) {
        log.info("Apply coupon request for code: {}", couponCode);

        CouponResponse response = couponService.applyCoupon(couponCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate a coupon (Admin only)
     */
    @PutMapping("/{couponId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> activateCoupon(@PathVariable Long couponId) {
        log.info("Activate coupon request for ID: {}", couponId);

        CouponResponse response = couponService.activateCoupon(couponId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate a coupon (Admin only)
     */
    @PutMapping("/{couponId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> deactivateCoupon(@PathVariable Long couponId) {
        log.info("Deactivate coupon request for ID: {}", couponId);

        CouponResponse response = couponService.deactivateCoupon(couponId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a coupon (Admin only)
     */
    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) {
        log.info("Delete coupon request for ID: {}", couponId);

        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}
