package com.library.user_management.controller;

import com.library.user_management.dto.*;
import com.library.user_management.entity.Role;
import com.library.user_management.entity.User;
import com.library.user_management.service.AuthenticationService;
import com.library.user_management.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User Controller
 * REST API endpoints for user management, authentication, and authorization
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Management & Authentication", description = "APIs for user authentication, registration, and management")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserDetailsServiceImpl userDetailsService;

    // ==================== AUTHENTICATION ENDPOINTS ====================

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        log.info("New registration request for email: {}", request.getEmail());
        try {
            AuthResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Authenticate user with credentials
     * POST /api/auth/login
     */
    @PostMapping("/auth/login")
    @Operation(summary = "Login user", description = "Authenticate user and receive JWT tokens")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        try {
            AuthResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    /**
     * Refresh access token
     * POST /api/auth/refresh
     */
    @PostMapping("/auth/refresh")
    @Operation(summary = "Refresh access token", description = "Get a new access token using refresh token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        log.info("Token refresh request");
        try {
            AuthResponse response = authenticationService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Verify email
     * POST /api/auth/verify-email
     */
    @PostMapping("/auth/verify-email")
    @Operation(summary = "Verify email", description = "Mark user email as verified")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> verifyEmail(Authentication authentication) {
        log.info("Email verification request for user: {}", authentication.getName());
        try {
            authenticationService.verifyEmail(authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Change password
     * POST /api/auth/change-password
     */
    @PostMapping("/auth/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        log.info("Password change request for user: {}", authentication.getName());
        try {
            
            authenticationService.changePassword(
                    request.get("username"),
                    request.get("newPassword")
            );
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Deactivate account
     * POST /api/auth/deactivate
     */
    @PostMapping("/auth/deactivate")
    @Operation(summary = "Deactivate account", description = "Deactivate user account")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deactivateAccount(Authentication authentication) {
        log.info("Account deactivation request for user: {}", authentication.getName());
        try {
            authenticationService.deactivateAccount(authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ==================== USER PROFILE ENDPOINTS ====================

    /**
     * Get current user profile
     * GET /api/users/profile
     */
    @GetMapping("/users/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        log.info("Profile request for user: {}", authentication.getName());
        try {
            UserResponse userResponse = authenticationService.getUserProfile(authentication.getName());
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by ID
     * GET /api/users/{userId}
     */
    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID (Admin or Librarian only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        log.info("User details request for ID: {}", userId);
        try {
            UserResponse userResponse = userDetailsService.getUserById(userId);
            return ResponseEntity.ok(userResponse);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== USER MANAGEMENT ENDPOINTS ====================

    /**
     * Get all active users
     * GET /api/users
     */
    @GetMapping("/users")
    @Operation(summary = "Get all active users", description = "Retrieve all active users (Admin or Librarian only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getAllActiveUsers() {
        log.info("Request for all active users");
        try {
            List<UserResponse> users = userDetailsService.getAllActiveUsers();
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            log.error("Error fetching users: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching users"));
        }
    }

    /**
     * Get users by role
     * GET /api/users/role/{role}
     */
    @GetMapping("/users/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieve users filtered by role (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        log.info("Request for users with role: {}", role);
        try {
            Role userRole = Role.valueOf("ROLE_" + role.toUpperCase());
            List<UserResponse> users = userDetailsService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role: " + role));
        }
    }

    /**
     * Get active users by role
     * GET /api/users/role/{role}/active
     */
    @GetMapping("/users/role/{role}/active")
    @Operation(summary = "Get active users by role", description = "Retrieve active users filtered by role (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getActiveUsersByRole(@PathVariable String role) {
        log.info("Request for active users with role: {}", role);
        try {
            Role userRole = Role.valueOf("ROLE_" + role.toUpperCase());
            List<UserResponse> users = userDetailsService.getActiveUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role: " + role));
        }
    }

    /**
     * Search users
     * GET /api/users/search?query={query}
     */
    @GetMapping("/users/search")
    @Operation(summary = "Search users", description = "Search users by name or email (Admin or Librarian only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        log.info("User search request with query: {}", query);
        try {
            List<UserResponse> users = userDetailsService.searchUsers(query);
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error searching users"));
        }
    }

    /**
     * Get verified users
     * GET /api/users/verified
     */
    @GetMapping("/users/verified")
    @Operation(summary = "Get verified users", description = "Retrieve all verified users (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getVerifiedUsers() {
        log.info("Request for verified users");
        try {
            List<UserResponse> users = userDetailsService.getVerifiedUsers();
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching verified users"));
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Admin test endpoint
     * GET /api/admin/test
     */
    @GetMapping("/admin/test")
    @Operation(summary = "Admin test", description = "Test endpoint for admin verification")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminTest() {
        log.info("Admin test endpoint accessed");
        return ResponseEntity.ok(Map.of("message", "Admin access granted"));
    }

    // ==================== PUBLIC ENDPOINTS ====================

    /**
     * Health check
     * GET /api/public/health
     */
    @GetMapping("/public/health")
    @Operation(summary = "Health check", description = "Check if the service is running")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "Service is running"));
    }
}
