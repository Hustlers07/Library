package com.library.user_management.entity;

public enum Floor {

    FLOOR_GF("GF", "Ground Floor"),
    FLOOR_FF("FIRST", "First Floor"),
    FLOOR_SF("SECOND", "Second Floor"),
    FLOOR_TF("THIRD", "Third Floor");


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
