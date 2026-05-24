package com.library.user_management.entity;

/**
 * Enum for audit action types
 */
public enum AuditAction {
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    RETRIEVE("Retrieve"),
    CONFIRM("Confirm"),
    CANCEL("Cancel"),
    PROCESS("Process"),
    REFUND("Refund"),
    LOGIN("Login"),
    LOGOUT("Logout"),
    RENEW("Renew"),
    ACTIVATE("Activate"),
    DEACTIVATE("Deactivate");

    private final String description;

    AuditAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
