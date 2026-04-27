package com.library.user_management.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Custom exception for JWT related errors
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class JwtException extends RuntimeException {
    private String message;

    public JwtException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
