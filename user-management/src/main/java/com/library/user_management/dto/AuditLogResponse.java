package com.library.user_management.dto;

import java.time.LocalDateTime;

import com.library.user_management.entity.AuditAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for audit log response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;

    private Long userId;

    private String username;

    private String entityType;

    private Long entityId;

    private AuditAction actionType;

    private String description;

    private String oldValues;

    private String newValues;

    private String ipAddress;

    private String httpMethod;

    private String requestUri;

    private Integer statusCode;

    private LocalDateTime createdAt;

    private Boolean isSuccess;

    private String errorMessage;
}
