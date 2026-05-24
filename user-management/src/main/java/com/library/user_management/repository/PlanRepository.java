package com.library.user_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.user_management.entity.Plan;
import com.library.user_management.entity.PlanType;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByPlanName(String planName);

    List<Plan> findByPlanType(PlanType planType);

    List<Plan> findByIsActiveTrue();

    @Query("SELECT p FROM Plan p WHERE p.isActive = true ORDER BY p.price ASC")
    List<Plan> findAllActivePlansOrderByPrice();

    @Query("SELECT p FROM Plan p WHERE p.planType = :planType AND p.isActive = true")
    List<Plan> findActivePlansByType(@Param("planType") PlanType planType);
}
