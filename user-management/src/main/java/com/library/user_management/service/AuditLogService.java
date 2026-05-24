package com.library.user_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.user_management.dto.AuditLogResponse;
import com.library.user_management.entity.AuditAction;
import com.library.user_management.entity.AuditLog;
import com.library.user_management.repository.AuditLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for audit log operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log an audit entry
     */
    public AuditLogResponse logAudit(Long userId, String username, String entityType, Long entityId,
            AuditAction actionType, String description, Object oldValues, Object newValues,
            String ipAddress, String httpMethod, String requestUri, Integer statusCode) {
        
        try {
            String oldValuesJson = oldValues != null ? objectMapper.writeValueAsString(oldValues) : null;
            String newValuesJson = newValues != null ? objectMapper.writeValueAsString(newValues) : null;

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .entityType(entityType)
                    .entityId(entityId)
                    .actionType(actionType)
                    .description(description)
                    .oldValues(oldValuesJson)
                    .newValues(newValuesJson)
                    .ipAddress(ipAddress)
                    .httpMethod(httpMethod)
                    .requestUri(requestUri)
                    .statusCode(statusCode)
                    .createdAt(LocalDateTime.now())
                    .isSuccess(true)
                    .build();

            AuditLog saved = auditLogRepository.save(auditLog);
            log.info("Audit logged - User: {}, Entity: {}, Action: {}", username, entityType, actionType);

            return mapToResponse(saved);
        } catch (Exception e) {
            log.error("Error logging audit for {}.{}: {}", entityType, entityId, e.getMessage(), e);
            throw new RuntimeException("Failed to log audit", e);
        }
    }

    /**
     * Log a failed operation
     */
    public AuditLogResponse logFailedOperation(Long userId, String username, String entityType, Long entityId,
            AuditAction actionType, String errorMessage, String ipAddress, String httpMethod,
            String requestUri, Integer statusCode) {
        
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .entityType(entityType)
                .entityId(entityId)
                .actionType(actionType)
                .ipAddress(ipAddress)
                .httpMethod(httpMethod)
                .requestUri(requestUri)
                .statusCode(statusCode)
                .createdAt(LocalDateTime.now())
                .isSuccess(false)
                .errorMessage(errorMessage)
                .build();

        AuditLog saved = auditLogRepository.save(auditLog);
        log.warn("Failed operation logged - User: {}, Entity: {}, Action: {}, Error: {}", 
                username, entityType, actionType, errorMessage);

        return mapToResponse(saved);
    }

    /**
     * Get audit logs for an entity
     */
    public List<AuditLogResponse> getEntityAuditLogs(String entityType, Long entityId) {
        List<AuditLog> logs = auditLogRepository.findByEntity(entityType, entityId);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get audit logs for an entity with pagination
     */
    public Page<AuditLogResponse> getEntityAuditLogs(String entityType, Long entityId, Pageable pageable) {
        Page<AuditLog> logs = auditLogRepository.findByEntity(entityType, entityId, pageable);
        return logs.map(this::mapToResponse);
    }

    /**
     * Get audit logs by user
     */
    public List<AuditLogResponse> getUserAuditLogs(Long userId) {
        List<AuditLog> logs = auditLogRepository.findByUserId(userId);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get audit logs by user with pagination
     */
    public Page<AuditLogResponse> getUserAuditLogs(Long userId, Pageable pageable) {
        Page<AuditLog> logs = auditLogRepository.findByUserId(userId, pageable);
        return logs.map(this::mapToResponse);
    }

    /**
     * Get audit logs by action type
     */
    public List<AuditLogResponse> getAuditLogsByAction(AuditAction actionType) {
        List<AuditLog> logs = auditLogRepository.findByActionType(actionType);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get audit logs by entity type
     */
    public List<AuditLogResponse> getAuditLogsByEntityType(String entityType) {
        List<AuditLog> logs = auditLogRepository.findByEntityType(entityType);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get audit logs by entity type with pagination
     */
    public Page<AuditLogResponse> getAuditLogsByEntityType(String entityType, Pageable pageable) {
        Page<AuditLog> logs = auditLogRepository.findByEntityType(entityType, pageable);
        return logs.map(this::mapToResponse);
    }

    /**
     * Get audit logs within date range
     */
    public List<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> logs = auditLogRepository.findByDateRange(startDate, endDate);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get failed operations
     */
    public List<AuditLogResponse> getFailedOperations() {
        List<AuditLog> logs = auditLogRepository.findFailedOperations();
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get audit log by ID
     */
    public AuditLogResponse getAuditLogById(Long auditLogId) {
        AuditLog log = auditLogRepository.findById(auditLogId)
                .orElseThrow(() -> new IllegalArgumentException("Audit log not found"));
        return mapToResponse(log);
    }

    /**
     * Get change count for entity
     */
    public Long getChangeCountForEntity(String entityType, Long entityId) {
        return auditLogRepository.countChangesForEntity(entityType, entityId);
    }

    /**
     * Get operation count for user
     */
    public Long getOperationCountForUser(Long userId) {
        return auditLogRepository.countOperationsByUser(userId);
    }

    /**
     * Map AuditLog to response DTO
     */
    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .actionType(auditLog.getActionType())
                .description(auditLog.getDescription())
                .oldValues(auditLog.getOldValues())
                .newValues(auditLog.getNewValues())
                .ipAddress(auditLog.getIpAddress())
                .httpMethod(auditLog.getHttpMethod())
                .requestUri(auditLog.getRequestUri())
                .statusCode(auditLog.getStatusCode())
                .createdAt(auditLog.getCreatedAt())
                .isSuccess(auditLog.getIsSuccess())
                .errorMessage(auditLog.getErrorMessage())
                .build();
    }
}
