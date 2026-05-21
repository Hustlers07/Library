package com.library.user_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.library.user_management.dto.PlanRequest;
import com.library.user_management.dto.PlanResponse;
import com.library.user_management.entity.Plan;
import com.library.user_management.entity.PlanType;
import com.library.user_management.repository.PlanRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanService {

    private final PlanRepository planRepository;

    /**
     * Create a new plan
     */
    public PlanResponse createPlan(PlanRequest planRequest) {
        log.info("Creating new plan: {}", planRequest.getPlanName());

        if (planRepository.findByPlanName(planRequest.getPlanName()).isPresent()) {
            throw new IllegalArgumentException("Plan with name '" + planRequest.getPlanName() + "' already exists");
        }

        Plan plan = Plan.builder()
                .planName(planRequest.getPlanName())
                .planType(planRequest.getPlanType())
                .description(planRequest.getDescription())
                .price(planRequest.getPrice())
                .validityDays(planRequest.getValidityDays())
                .hourlyLimit(planRequest.getHourlyLimit())
                .dailyLimit(planRequest.getDailyLimit())
                .monthlyLimit(planRequest.getMonthlyLimit())
                .floorAccessAllowed(planRequest.getFloorAccessAllowed() != null ? planRequest.getFloorAccessAllowed() : true)
                .seatAccessAllowed(planRequest.getSeatAccessAllowed() != null ? planRequest.getSeatAccessAllowed() : true)
                .discountPercentage(planRequest.getDiscountPercentage() != null ? planRequest.getDiscountPercentage() : java.math.BigDecimal.ZERO)
                .build();

        Plan savedPlan = planRepository.save(plan);
        log.info("Plan created with ID: {}", savedPlan.getId());

        return mapToResponse(savedPlan);
    }

    /**
     * Update an existing plan
     */
    public PlanResponse updatePlan(Long planId, PlanRequest planRequest) {
        log.info("Updating plan with ID: {}", planId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        plan.setPlanName(planRequest.getPlanName());
        plan.setPlanType(planRequest.getPlanType());
        plan.setDescription(planRequest.getDescription());
        plan.setPrice(planRequest.getPrice());
        plan.setValidityDays(planRequest.getValidityDays());
        plan.setHourlyLimit(planRequest.getHourlyLimit());
        plan.setDailyLimit(planRequest.getDailyLimit());
        plan.setMonthlyLimit(planRequest.getMonthlyLimit());
        plan.setFloorAccessAllowed(planRequest.getFloorAccessAllowed());
        plan.setSeatAccessAllowed(planRequest.getSeatAccessAllowed());
        plan.setDiscountPercentage(planRequest.getDiscountPercentage());
        plan.setUpdatedAt(LocalDateTime.now());

        Plan updatedPlan = planRepository.save(plan);
        log.info("Plan updated: {}", planId);

        return mapToResponse(updatedPlan);
    }

    /**
     * Get plan by ID
     */
    public PlanResponse getPlanById(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        return mapToResponse(plan);
    }

    /**
     * Get all plans
     */
    public List<PlanResponse> getAllPlans() {
        log.info("Fetching all plans");
        List<Plan> plans = planRepository.findAll();
        return plans.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get all active plans
     */
    public List<PlanResponse> getActivePlans() {
        log.info("Fetching all active plans");
        List<Plan> plans = planRepository.findByIsActiveTrue();
        return plans.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get plans by type
     */
    public List<PlanResponse> getPlansByType(PlanType planType) {
        log.info("Fetching plans by type: {}", planType);
        List<Plan> plans = planRepository.findActivePlansByType(planType);
        return plans.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Activate a plan
     */
    public PlanResponse activatePlan(Long planId) {
        log.info("Activating plan with ID: {}", planId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        plan.setIsActive(true);
        plan.setUpdatedAt(LocalDateTime.now());

        Plan updatedPlan = planRepository.save(plan);
        log.info("Plan activated: {}", planId);

        return mapToResponse(updatedPlan);
    }

    /**
     * Deactivate a plan
     */
    public PlanResponse deactivatePlan(Long planId) {
        log.info("Deactivating plan with ID: {}", planId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        plan.setIsActive(false);
        plan.setUpdatedAt(LocalDateTime.now());

        Plan updatedPlan = planRepository.save(plan);
        log.info("Plan deactivated: {}", planId);

        return mapToResponse(updatedPlan);
    }

    /**
     * Delete a plan
     */
    public void deletePlan(Long planId) {
        log.info("Deleting plan with ID: {}", planId);

        if (!planRepository.existsById(planId)) {
            throw new IllegalArgumentException("Plan not found");
        }

        planRepository.deleteById(planId);
        log.info("Plan deleted: {}", planId);
    }

    /**
     * Map Plan entity to response DTO
     */
    private PlanResponse mapToResponse(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .planName(plan.getPlanName())
                .planType(plan.getPlanType())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .validityDays(plan.getValidityDays())
                .hourlyLimit(plan.getHourlyLimit())
                .dailyLimit(plan.getDailyLimit())
                .monthlyLimit(plan.getMonthlyLimit())
                .floorAccessAllowed(plan.getFloorAccessAllowed())
                .seatAccessAllowed(plan.getSeatAccessAllowed())
                .isActive(plan.getIsActive())
                .discountPercentage(plan.getDiscountPercentage())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}
