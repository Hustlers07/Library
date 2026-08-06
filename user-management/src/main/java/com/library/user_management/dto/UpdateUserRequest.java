package com.library.user_management.dto;

import com.library.user_management.entity.Role;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest extends UserRegistrationRequest {

    private Role role;
}
