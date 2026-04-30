package com.library.user_management.service;

import com.library.user_management.dto.AuthRequest;
import com.library.user_management.dto.AuthResponse;
import com.library.user_management.dto.UserRegistrationRequest;
import com.library.user_management.dto.UserResponse;
import com.library.user_management.entity.Role;
import com.library.user_management.entity.User;
import com.library.user_management.repository.UserRepository;
import com.library.user_management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Authentication Service
 * Handles user authentication, registration, and token management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     */
    public AuthResponse register(UserRegistrationRequest request) {
        // Validate input
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }


        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? request.getRole() : Role.ROLE_MEMBER)
                .isActive(true)
                .isVerifiedEmail(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered with email: {}", savedUser.getEmail());

        // Generate tokens
        String token = jwtTokenProvider.generateToken(savedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getUsername(), savedUser.getEmail());

        return buildAuthResponse(savedUser, token, refreshToken);
    }

    /**
     * Authenticate user with username and password
     */
    public AuthResponse authenticate(AuthRequest request) {
        // Authentication authentication = authenticationManager.authenticate(
        // new UsernamePasswordAuthenticationToken(
        // request.getUsername(),
        // request.getPassword()
        // )
        // );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify password using PasswordEncoder
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername(), user.getEmail());

        log.info("User authenticated successfully: {}", user.getUsername());

        return buildAuthResponse(user, token, refreshToken);
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.isTokenValid(refreshToken, extractEmailFromRefreshToken(refreshToken))) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername(), user.getEmail());

        log.info("Token refreshed for user: {}", user.getUsername());

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    /**
     * Get user profile
     */
    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mapToUserResponse(user);
    }

    /**
     * Verify email
     */
    public void verifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsVerifiedEmail(true);
        userRepository.save(user);
        log.info("Email verified for user: {}", email);
    }

    /**
     * Change password
     */
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", email);
    }

    /**
     * Deactivate account
     */
    public void deactivateAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
        log.info("Account deactivated for user: {}", email);
    }

    /**
     * Helper method to build AuthResponse
     */
    private AuthResponse buildAuthResponse(User user, String token, String refreshToken) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isVerified(user.getIsVerifiedEmail())
                .expiresIn(formatExpiryTime(jwtTokenProvider.getTokenExpirationMs()))
                .build();
    }

    /**
     * Helper method to map User to UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerifiedEmail(user.getIsVerifiedEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    /**
     * Helper method to extract email from refresh token
     */
    private String extractEmailFromRefreshToken(String refreshToken) {
        return jwtTokenProvider.extractEmail(refreshToken);
    }

    /**
     * Format expiry time to human readable format
     */
    private String formatExpiryTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        return hours + " hours";
    }
}
