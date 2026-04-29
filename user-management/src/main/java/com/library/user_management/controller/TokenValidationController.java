package com.library.user_management.controller;

import com.library.user_management.dto.TokenValidationResponse;
import com.library.user_management.entity.User;
import com.library.user_management.repository.UserRepository;
import com.library.user_management.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Token Validation Controller
 * Provides centralized JWT token validation for microservices
 */
@Slf4j
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@Tag(name = "Internal Service APIs", description = "APIs for inter-service communication")
public class TokenValidationController {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    /**
     * Validate JWT token
     * POST /api/internal/validate-token
     * Internal endpoint for other microservices to validate tokens
     */
    @PostMapping("/validate-token")
    @Operation(summary = "Validate JWT token", description = "Validate JWT token for other microservices")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.debug("Token validation request received");

        try {
            // Extract token from header
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid authorization header format");
                return ResponseEntity.ok(TokenValidationResponse.builder()
                        .valid(false)
                        .message("Invalid authorization header format")
                        .build());
            }

            String token = authHeader.substring("Bearer ".length());

            // Validate token
            if (!StringUtils.hasText(token)) {
                log.warn("Empty token provided");
                return ResponseEntity.ok(TokenValidationResponse.builder()
                        .valid(false)
                        .message("Token is empty")
                        .build());
            }

            // Extract username and validate
            String username = tokenProvider.extractUsername(token);
            if (!StringUtils.hasText(username)) {
                log.warn("Could not extract username from token");
                return ResponseEntity.ok(TokenValidationResponse.builder()
                        .valid(false)
                        .message("Could not extract username from token")
                        .build());
            }

            // Check if token is valid
            if (!tokenProvider.isTokenValid(token, username)) {
                log.warn("Token validation failed for user: {}", username);
                return ResponseEntity.ok(TokenValidationResponse.builder()
                        .valid(false)
                        .message("Token is invalid or expired")
                        .build());
            }

            // Get user details from database
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user == null) {
                log.warn("User not found in database: {}", username);
                return ResponseEntity.ok(TokenValidationResponse.builder()
                        .valid(false)
                        .message("User not found")
                        .build());
            }

            // Return validation response with user details
            log.info("Token validation successful for user: {}", username);
            return ResponseEntity.ok(TokenValidationResponse.builder()
                    .valid(true)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .userId(user.getId())
                    .message("Token is valid")
                    .build());

        } catch (Exception ex) {
            log.error("Error validating token: {}", ex.getMessage());
            return ResponseEntity.ok(TokenValidationResponse.builder()
                    .valid(false)
                    .message("Error validating token: " + ex.getMessage())
                    .build());
        }
    }

    /**
     * Health check for authentication service
     * GET /api/internal/auth-health
     */
    @GetMapping("/auth-health")
    @Operation(summary = "Health check", description = "Check if authentication service is running")
    public ResponseEntity<?> authHealth() {
        log.debug("Auth health check requested");
        return ResponseEntity.ok(java.util.Map.of(
                "status", "Authentication service is running",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
