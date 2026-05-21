package com.library.user_management.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.user_management.dto.AuditLogResponse;
import com.library.user_management.entity.AuditAction;
import com.library.user_management.service.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for audit logs
 */
@Slf4j
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Get audit logs for a specific entity
     * Only ADMIN and LIBRARIAN can view
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<AuditLogResponse>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        log.info("Fetching audit logs for {}:{}", entityType, entityId);

        List<AuditLogResponse> logs = auditLogService.getEntityAuditLogs(entityType, entityId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs for a specific entity with pagination
     */
    @GetMapping("/entity/{entityType}/{entityId}/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Page<AuditLogResponse>> getEntityAuditLogsPage(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            Pageable pageable) {
        log.info("Fetching paginated audit logs for {}:{}", entityType, entityId);

        Page<AuditLogResponse> logs = auditLogService.getEntityAuditLogs(entityType, entityId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs by user
     * Admin can view all, users can only view their own
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MEMBER') and #userId == authentication.principal.id)")
    public ResponseEntity<List<AuditLogResponse>> getUserAuditLogs(
            @PathVariable Long userId) {
        log.info("Fetching audit logs for user: {}", userId);

        List<AuditLogResponse> logs = auditLogService.getUserAuditLogs(userId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs by user with pagination
     */
    @GetMapping("/user/{userId}/page")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MEMBER') and #userId == authentication.principal.id)")
    public ResponseEntity<Page<AuditLogResponse>> getUserAuditLogsPage(
            @PathVariable Long userId,
            Pageable pageable) {
        log.info("Fetching paginated audit logs for user: {}", userId);

        Page<AuditLogResponse> logs = auditLogService.getUserAuditLogs(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs by action type
     * Only ADMIN can view
     */
    @GetMapping("/action/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByAction(
            @PathVariable AuditAction actionType) {
        log.info("Fetching audit logs for action: {}", actionType);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByAction(actionType);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs by entity type
     * Only ADMIN and LIBRARIAN can view
     */
    @GetMapping("/entity-type/{entityType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntityType(
            @PathVariable String entityType) {
        log.info("Fetching audit logs for entity type: {}", entityType);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByEntityType(entityType);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs by entity type with pagination
     */
    @GetMapping("/entity-type/{entityType}/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByEntityTypePage(
            @PathVariable String entityType,
            Pageable pageable) {
        log.info("Fetching paginated audit logs for entity type: {}", entityType);

        Page<AuditLogResponse> logs = auditLogService.getAuditLogsByEntityType(entityType, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs within date range
     * Only ADMIN can view
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching audit logs between {} and {}", startDate, endDate);

        List<AuditLogResponse> logs = auditLogService.getAuditLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get failed operations
     * Only ADMIN can view
     */
    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getFailedOperations() {
        log.info("Fetching failed operations");

        List<AuditLogResponse> logs = auditLogService.getFailedOperations();
        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit log by ID
     * Only ADMIN and the user who performed the action can view
     */
    @GetMapping("/{auditLogId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<AuditLogResponse> getAuditLogById(
            @PathVariable Long auditLogId) {
        log.info("Fetching audit log: {}", auditLogId);

        AuditLogResponse log = auditLogService.getAuditLogById(auditLogId);
        return ResponseEntity.ok(log);
    }

    /**
     * Get change count for entity
     * Only ADMIN and LIBRARIAN can view
     */
    @GetMapping("/change-count/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Long> getChangeCountForEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        log.info("Fetching change count for {}:{}", entityType, entityId);

        Long count = auditLogService.getChangeCountForEntity(entityType, entityId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get operation count for user
     * Only ADMIN can view
     */
    @GetMapping("/operation-count/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getOperationCountForUser(
            @PathVariable Long userId) {
        log.info("Fetching operation count for user: {}", userId);

        Long count = auditLogService.getOperationCountForUser(userId);
        return ResponseEntity.ok(count);
    }
}
