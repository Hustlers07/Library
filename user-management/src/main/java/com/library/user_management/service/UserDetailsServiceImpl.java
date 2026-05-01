package com.library.user_management.service;

import com.library.user_management.dto.UserResponse;
import com.library.user_management.entity.Role;
import com.library.user_management.entity.User;
import com.library.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Details Service
 * Provides user details for Spring Security authentication
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService, UserProfileDetailsService  {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }

    /**
     * Get user by Email
     */
    @Override
    public User loadByEmail(String email) throws Exception {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new Exception("User not found with email: " + email);
                });
    }

    /**
     * Get user by ID
     */
    @Override
    public UserResponse getUserById(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        return mapToUserResponse(user);
    }

    /**
     * Get all active users
     */
    @Override
    public List<UserResponse> getAllActiveUsers() {
        return userRepository.findAllActiveUsers()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     */
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active users by role
     */
    public List<UserResponse> getActiveUsersByRole(Role role) {
        return userRepository.findActiveUsersByRole(role)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search users
     */
    public List<UserResponse> searchUsers(String query) {
        return userRepository.searchUsers(query)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get verified users
     */
    public List<UserResponse> getVerifiedUsers() {
        return userRepository.findVerifiedUsers()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
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
}
