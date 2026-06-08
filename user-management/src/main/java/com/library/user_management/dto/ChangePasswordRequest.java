package com.library.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for change password request
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private String username;

    private String newPassword;

}
