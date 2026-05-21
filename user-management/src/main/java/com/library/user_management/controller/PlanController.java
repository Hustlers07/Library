package com.library.user_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.library.user_management.dto.PlanRequest;
import com.library.user_management.dto.PlanResponse;
import com.library.user_management.entity.PlanType;
import com.library.user_management.service.PlanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user-management/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /**
     * Create a new plan (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest planRequest) {
        log.info("Create plan request received");

        PlanResponse response = planService.createPlan(planRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update a plan (Admin only)
     */
    @PutMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable Long planId,
            @RequestBody PlanRequest planRequest) {
        log.info("Update plan request for ID: {}", planId);

        PlanResponse response = planService.updatePlan(planId, planRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get plan by ID
     */
    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long planId) {
        log.info("Get plan request for ID: {}", planId);

        PlanResponse response = planService.getPlanById(planId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all plans
     */
    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        log.info("Get all plans request");

        List<PlanResponse> response = planService.getAllPlans();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active plans
     */
    @GetMapping("/active")
    public ResponseEntity<List<PlanResponse>> getActivePlans() {
        log.info("Get active plans request");

        List<PlanResponse> response = planService.getActivePlans();
        return ResponseEntity.ok(response);
    }

    /**
     * Get plans by type
     */
    @GetMapping("/type/{planType}")
    public ResponseEntity<List<PlanResponse>> getPlansByType(@PathVariable PlanType planType) {
        log.info("Get plans by type: {}", planType);

        List<PlanResponse> response = planService.getPlansByType(planType);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate a plan (Admin only)
     */
    @PutMapping("/{planId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> activatePlan(@PathVariable Long planId) {
        log.info("Activate plan request for ID: {}", planId);

        PlanResponse response = planService.activatePlan(planId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate a plan (Admin only)
     */
    @PutMapping("/{planId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> deactivatePlan(@PathVariable Long planId) {
        log.info("Deactivate plan request for ID: {}", planId);

        PlanResponse response = planService.deactivatePlan(planId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a plan (Admin only)
     */
    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        log.info("Delete plan request for ID: {}", planId);

        planService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }
}
