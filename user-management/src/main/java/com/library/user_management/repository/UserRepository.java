package com.library.user_management.repository;

import com.library.user_management.entity.Role;
import com.library.user_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 * Provides database access operations for users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find all active users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();

    /**
     * Find users by role
     */
    List<User> findByRole(Role role);

    /**
     * Find active users by role
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role = :role")
    List<User> findActiveUsersByRole(@Param("role") Role role);

    /**
     * Find verified users
     */
    @Query("SELECT u FROM User u WHERE u.isVerifiedEmail = true AND u.isActive = true")
    List<User> findVerifiedUsers();

    /**
     * Find unverified users
     */
    @Query("SELECT u FROM User u WHERE u.isVerifiedEmail = false AND u.isActive = true")
    List<User> findUnverifiedUsers();

    /**
     * Search users by name or email
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (u.firstName LIKE %:query% OR u.lastName LIKE %:query% OR u.email LIKE %:query%)")
    List<User> searchUsers(@Param("query") String query);
}
