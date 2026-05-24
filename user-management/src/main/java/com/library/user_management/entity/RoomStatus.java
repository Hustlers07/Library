package com.library.user_management.entity;

public enum RoomStatus {

    ROOM_VACANT("VACANT","Room is vacant."),
    ROOM_MAINTENANCE("MAINTAINANCE","Room is under maintenance."),
    ROOM_SEATING("SEATING","Room is converted to seating arrangement."),
    ROOM_OCCUPIED("OCCUPIED","Room is occupied"),
    ROOM_DISCARDED("DISCARDED","Room is discarded");

    private final String displayName;
    private final String description;

    RoomStatus(String displayName, String description) {
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
