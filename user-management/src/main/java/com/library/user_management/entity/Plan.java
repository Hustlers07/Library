package com.library.user_management.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Plan Entity - Represents subscription plans for users
 */
@Entity
@Table(name = "plans", uniqueConstraints = {
    @UniqueConstraint(columnNames = "plan_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer validityDays;

    @Column(nullable = false)
    private Integer hourlyLimit; // Hourly booking limit

    @Column(nullable = false)
    private Integer dailyLimit; // Daily booking limit

    @Column(nullable = false)
    private Integer monthlyLimit; // Monthly booking limit

    @Column(nullable = false)
    private Boolean floorAccessAllowed = true;

    @Column(nullable = false)
    private Boolean seatAccessAllowed = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
