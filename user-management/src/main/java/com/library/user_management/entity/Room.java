package com.library.user_management.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"house_no", "floor", "location"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoomStatus status =RoomStatus.ROOM_VACANT;

    @Column(nullable=false, name = "house_no")
    private String houseNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    @Builder.Default
    private Floor floor = Floor.FLOOR_GF;

    @Column(nullable=false )
    private String location;

    @Column(nullable=false)
    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Seat> seats;

    @ManyToMany(mappedBy = "rooms", cascade = CascadeType.ALL)
    private List<User> user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
