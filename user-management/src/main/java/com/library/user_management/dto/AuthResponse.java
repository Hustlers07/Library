package com.library.user_management.dto;

import com.library.user_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isVerified;
    private String expiresIn;
}
