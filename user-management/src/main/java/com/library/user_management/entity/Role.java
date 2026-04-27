package com.library.user_management.entity;

/**
 * Enum representing user roles in the system.
 * Defines authorization levels for different user types.
 */
public enum Role {
    ROLE_ADMIN("ADMIN", "Administrator with full system access"),
    ROLE_LIBRARIAN("LIBRARIAN", "Librarian with library management permissions"),
    ROLE_MEMBER("MEMBER", "Regular member with limited access");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
