package com.library.user_management.entity;

public enum Floor {

    FLOOR_GF("GF", "Ground Floor"),
    FLOOR_FIRST("FIRST", "First Floor"),
    FLOOR_SECOND("SECOND", "Second Floor"),
    FLOOR_THIRD("THIRD", "Third Floor");


    private final String displayName;
    private final String description;

    Floor(String displayName, String description){
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
