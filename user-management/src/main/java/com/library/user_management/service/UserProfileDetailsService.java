package com.library.user_management.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.library.user_management.dto.UserResponse;

public interface UserProfileDetailsService {

    public UserDetails loadByEmail(String email) throws Exception;
    public UserResponse getUserById(Long userId) throws Exception;
    public List<UserResponse> getAllActiveUsers();

}
