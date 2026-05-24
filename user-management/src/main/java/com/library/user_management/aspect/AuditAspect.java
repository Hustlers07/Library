package com.library.user_management.aspect;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.library.user_management.entity.AuditAction;
import com.library.user_management.security.JwtTokenProvider;
import com.library.user_management.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AOP Aspect for automatic audit logging
 * Logs all controller and service method calls
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Object>> requestContext = ThreadLocal.withInitial(HashMap::new);

    /**
     * Before method execution - capture context
     */
    @Before("execution(* com.library.user_management.controller.*.*(..))")
    public void beforeController(JoinPoint joinPoint) {
        captureContext();
        startTime.set(System.currentTimeMillis());
        log.debug("Controller method called: {}.{}", joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    /**
     * After successful method execution - log success
     */
    @AfterReturning(pointcut = "execution(* com.library.user_management.controller.*.*(..))", returning = "result")
    public void afterControllerReturning(JoinPoint joinPoint, Object result) {
        try {
            long duration = System.currentTimeMillis() - startTime.get();
            String methodName = joinPoint.getSignature().getName();
            
            // Determine action type based on method name
            AuditAction actionType = determineActionType(methodName);
            
            Map<String, Object> context = requestContext.get();
            
            auditLogService.logAudit(
                    userId.get() != null ? userId.get() : 0L,
                    username.get() != null ? username.get() : "SYSTEM",
                    extractEntityType(joinPoint),
                    extractEntityId(joinPoint),
                    actionType,
                    String.format("%s in %dms", methodName, duration),
                    null,
                    null,
                    (String) context.get("ipAddress"),
                    (String) context.get("httpMethod"),
                    (String) context.get("requestUri"),
                    200
            );
        } catch (Exception e) {
            log.error("Error logging audit after controller execution", e);
        } finally {
            cleanup();
        }
    }

    /**
     * After throwing exception - log failure
     */
    @AfterThrowing(pointcut = "execution(* com.library.user_management.controller.*.*(..))", throwing = "exception")
    public void afterControllerThrowing(JoinPoint joinPoint, Throwable exception) {
        try {
            String methodName = joinPoint.getSignature().getName();
            AuditAction actionType = determineActionType(methodName);
            
            Map<String, Object> context = requestContext.get();
            
            auditLogService.logFailedOperation(
                    userId.get() != null ? userId.get() : 0L,
                    username.get() != null ? username.get() : "SYSTEM",
                    extractEntityType(joinPoint),
                    0L,
                    actionType,
                    exception.getMessage(),
                    (String) context.get("ipAddress"),
                    (String) context.get("httpMethod"),
                    (String) context.get("requestUri"),
                    400
            );
        } catch (Exception e) {
            log.error("Error logging failed audit after controller execution", e);
        } finally {
            cleanup();
        }
    }

    /**
     * Capture context from HTTP request
     */
    private void captureContext() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                Map<String, Object> context = new HashMap<>();
                context.put("ipAddress", getClientIpAddress(request));
                context.put("httpMethod", request.getMethod());
                context.put("requestUri", request.getRequestURI());
                
                requestContext.set(context);
                
                // Extract user info from JWT token
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        userId.set(jwtTokenProvider.getUserIdFromToken(token));
                        username.set(jwtTokenProvider.extractUsername(token));
                    } catch (Exception e) {
                        log.debug("Could not extract user info from token");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not capture context: {}", e.getMessage());
        }
    }

    /**
     * Determine action type from method name
     */
    private AuditAction determineActionType(String methodName) {
        if (methodName.contains("create")) {
            return AuditAction.CREATE;
        } else if (methodName.contains("update")) {
            return AuditAction.UPDATE;
        } else if (methodName.contains("delete")) {
            return AuditAction.DELETE;
        } else if (methodName.contains("confirm")) {
            return AuditAction.CONFIRM;
        } else if (methodName.contains("cancel")) {
            return AuditAction.CANCEL;
        } else if (methodName.contains("process")) {
            return AuditAction.PROCESS;
        } else if (methodName.contains("refund")) {
            return AuditAction.REFUND;
        } else if (methodName.contains("renew")) {
            return AuditAction.RENEW;
        } else if (methodName.contains("activate")) {
            return AuditAction.ACTIVATE;
        } else if (methodName.contains("deactivate")) {
            return AuditAction.DEACTIVATE;
        } else {
            return AuditAction.RETRIEVE;
        }
    }

    /**
     * Extract entity type from join point
     */
    private String extractEntityType(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        // Remove "Controller" suffix
        return className.replace("Controller", "");
    }

    /**
     * Extract entity ID from method arguments
     */
    private Long extractEntityId(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return 0L;
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Cleanup thread local variables
     */
    private void cleanup() {
        startTime.remove();
        userId.remove();
        username.remove();
        requestContext.remove();
    }
}
