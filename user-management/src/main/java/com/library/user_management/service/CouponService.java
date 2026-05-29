package com.library.user_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.library.user_management.dto.CouponRequest;
import com.library.user_management.dto.CouponResponse;
import com.library.user_management.entity.Coupon;
import com.library.user_management.repository.CouponRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    /**
     * Create a new coupon
     */
    public CouponResponse createCoupon(CouponRequest couponRequest) {
        log.info("Creating new coupon with code: {}", couponRequest.getCouponCode());

        if (couponRepository.findByCouponCode(couponRequest.getCouponCode()).isPresent()) {
            throw new IllegalArgumentException("Coupon with code '" + couponRequest.getCouponCode() + "' already exists");
        }

        Coupon coupon = Coupon.builder()
                .couponCode(couponRequest.getCouponCode().toUpperCase())
                .description(couponRequest.getDescription())
                .discountPercentage(couponRequest.getDiscountPercentage())
                .discountAmount(couponRequest.getDiscountAmount())
                .minimumBookingAmount(couponRequest.getMinimumBookingAmount())
                .maximumDiscountAmount(couponRequest.getMaximumDiscountAmount())
                .usageLimit(couponRequest.getUsageLimit())
                .validFrom(couponRequest.getValidFrom())
                .validTill(couponRequest.getValidTill())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Coupon created with ID: {}", savedCoupon.getId());

        return mapToResponse(savedCoupon);
    }

    /**
     * Update an existing coupon
     */
    public CouponResponse updateCoupon(Long couponId, CouponRequest couponRequest) {
        log.info("Updating coupon with ID: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        coupon.setCouponCode(couponRequest.getCouponCode().toUpperCase());
        coupon.setDescription(couponRequest.getDescription());
        coupon.setDiscountPercentage(couponRequest.getDiscountPercentage());
        coupon.setDiscountAmount(couponRequest.getDiscountAmount());
        coupon.setMinimumBookingAmount(couponRequest.getMinimumBookingAmount());
        coupon.setMaximumDiscountAmount(couponRequest.getMaximumDiscountAmount());
        coupon.setUsageLimit(couponRequest.getUsageLimit());
        coupon.setValidFrom(couponRequest.getValidFrom());
        coupon.setValidTill(couponRequest.getValidTill());
        coupon.setUpdatedAt(LocalDateTime.now());

        Coupon updatedCoupon = couponRepository.save(coupon);
        log.info("Coupon updated: {}", couponId);

        return mapToResponse(updatedCoupon);
    }

    /**
     * Get coupon by ID
     */
    public CouponResponse getCouponById(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        return mapToResponse(coupon);
    }

    /**
     * Get coupon by code
     */
    public CouponResponse getCouponByCode(String couponCode) {
        log.info("Fetching coupon by code: {}", couponCode);
        Coupon coupon = couponRepository.findValidCoupon(couponCode.toUpperCase(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Active coupon not found or expired"));
        return mapToResponse(coupon);
    }

    /**
     * Get all coupons
     */
    public List<CouponResponse> getAllCoupons() {
        log.info("Fetching all coupons");
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get all active coupons
     */
    public List<CouponResponse> getActiveCoupons() {
        log.info("Fetching all active coupons");
        List<Coupon> coupons = couponRepository.findByIsActiveTrue();
        return coupons.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Apply coupon - increment usage count
     */
    public CouponResponse applyCoupon(String couponCode) {
        log.info("Applying coupon with code: {}", couponCode);

        Coupon coupon = couponRepository.findValidCoupon(couponCode.toUpperCase(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found or expired"));

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("Coupon usage limit exceeded");
        }

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        coupon.setUpdatedAt(LocalDateTime.now());

        Coupon updatedCoupon = couponRepository.save(coupon);
        log.info("Coupon applied successfully: {}", couponCode);

        return mapToResponse(updatedCoupon);
    }

    /**
     * Activate a coupon
     */
    public CouponResponse activateCoupon(Long couponId) {
        log.info("Activating coupon with ID: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        coupon.setIsActive(true);
        coupon.setUpdatedAt(LocalDateTime.now());

        Coupon updatedCoupon = couponRepository.save(coupon);
        log.info("Coupon activated: {}", couponId);

        return mapToResponse(updatedCoupon);
    }

    /**
     * Deactivate a coupon
     */
    public CouponResponse deactivateCoupon(Long couponId) {
        log.info("Deactivating coupon with ID: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        coupon.setIsActive(false);
        coupon.setUpdatedAt(LocalDateTime.now());

        Coupon updatedCoupon = couponRepository.save(coupon);
        log.info("Coupon deactivated: {}", couponId);

        return mapToResponse(updatedCoupon);
    }

    /**
     * Delete a coupon
     */
    public void deleteCoupon(Long couponId) {
        log.info("Deleting coupon with ID: {}", couponId);

        if (!couponRepository.existsById(couponId)) {
            throw new IllegalArgumentException("Coupon not found");
        }

        couponRepository.deleteById(couponId);
        log.info("Coupon deleted: {}", couponId);
    }

    /**
     * Map Coupon entity to response DTO
     */
    private CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .couponCode(coupon.getCouponCode())
                .description(coupon.getDescription())
                .discountPercentage(coupon.getDiscountPercentage())
                .discountAmount(coupon.getDiscountAmount())
                .minimumBookingAmount(coupon.getMinimumBookingAmount())
                .maximumDiscountAmount(coupon.getMaximumDiscountAmount())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .validFrom(coupon.getValidFrom())
                .validTill(coupon.getValidTill())
                .isActive(coupon.getIsActive())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }
}
