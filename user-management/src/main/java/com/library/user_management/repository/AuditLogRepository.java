package com.library.user_management.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.user_management.entity.AuditAction;
import com.library.user_management.entity.AuditLog;

/**
 * Repository for audit logs
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find all audit logs for an entity
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<AuditLog> findByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * Find all audit logs for an entity with pagination
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId, Pageable pageable);

    /**
     * Find audit logs by user
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserId(@Param("userId") Long userId);

    /**
     * Find audit logs by user with pagination
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.createdAt DESC")
    Page<AuditLog> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find audit logs by action type
     */
    @Query("SELECT a FROM AuditLog a WHERE a.actionType = :actionType ORDER BY a.createdAt DESC")
    List<AuditLog> findByActionType(@Param("actionType") AuditAction actionType);

    /**
     * Find audit logs by entity type
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType ORDER BY a.createdAt DESC")
    List<AuditLog> findByEntityType(@Param("entityType") String entityType);

    /**
     * Find audit logs within date range
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find failed operations
     */
    @Query("SELECT a FROM AuditLog a WHERE a.isSuccess = false ORDER BY a.createdAt DESC")
    List<AuditLog> findFailedOperations();

    /**
     * Find audit logs by entity type with pagination
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType ORDER BY a.createdAt DESC")
    Page<AuditLog> findByEntityType(@Param("entityType") String entityType, Pageable pageable);

    /**
     * Count changes by entity
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId")
    Long countChangesForEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * Count operations by user
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId")
    Long countOperationsByUser(@Param("userId") Long userId);
}
