package com.library.user_management.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.user_management.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCouponCode(String couponCode);

    List<Coupon> findByIsActiveTrue();

    @Query("SELECT c FROM Coupon c WHERE c.couponCode = :code AND c.isActive = true AND c.validFrom <= :now AND c.validTill >= :now")
    Optional<Coupon> findValidCoupon(@Param("code") String couponCode, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.validTill < :now")
    List<Coupon> findExpiredCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.usageLimit IS NOT NULL AND c.usedCount >= c.usageLimit")
    List<Coupon> findExhaustedCoupons();

    @Query("SELECT c FROM Coupon c WHERE c.validFrom BETWEEN :startDate AND :endDate")
    List<Coupon> findCouponsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
