package com.library.user_management.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for audit trail tracking
 * Records all changes to entities in the system
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_timestamp", columnList = "created_at"),
    @Index(name = "idx_audit_action", columnList = "action_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of user who made the change
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Username of user who made the change
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Type of entity being changed (e.g., Booking, Payment, User, etc.)
     */
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    /**
     * ID of the entity being changed
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * Type of action performed
     */
    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction actionType;

    /**
     * Description of the change
     */
    @Column(name = "description")
    private String description;

    /**
     * Old values (JSON format)
     */
    @Lob
    @Column(name = "old_values")
    private String oldValues;

    /**
     * New values (JSON format)
     */
    @Lob
    @Column(name = "new_values")
    private String newValues;

    /**
     * IP address of the request
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * HTTP method used
     */
    @Column(name = "http_method")
    private String httpMethod;

    /**
     * Request endpoint/URI
     */
    @Column(name = "request_uri")
    private String requestUri;

    /**
     * HTTP status code
     */
    @Column(name = "status_code")
    private Integer statusCode;

    /**
     * Timestamp of the change
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    private Boolean isSuccess = true;

    /**
     * Error message if action failed
     */
    @Column(name = "error_message")
    private String errorMessage;
}
