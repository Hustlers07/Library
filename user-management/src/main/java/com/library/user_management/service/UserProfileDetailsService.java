package com.library.user_management.service;

import java.util.List;

import com.library.user_management.dto.UserResponse;
import com.library.user_management.entity.User;

public interface UserProfileDetailsService {

    public User loadByEmail(String email) throws Exception;
    public UserResponse getUserById(Long userId) throws Exception;
    public List<UserResponse> getAllActiveUsers();
    public UserResponse getUserByUsername(String username) throws Exception;

}
