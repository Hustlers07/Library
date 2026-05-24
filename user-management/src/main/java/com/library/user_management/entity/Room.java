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
    @Column(nullable=false, length = 20)
    @Builder.Default
    private Floor floor = Floor.FLOOR_GF;

    @Column(nullable=false )
    private String location;

    @Column(nullable=false)
    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Seat> seats;

    @ManyToMany
     @JoinTable(
        name = "user_room", // join table name
        joinColumns = @JoinColumn(
            name = "room_id", referencedColumnName = "room_id"
        ),
        inverseJoinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "user_id"
        ),
        foreignKey = @ForeignKey(name="FK_user_room")
    )
    private List<User> user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        toUpperCase();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        toUpperCase();
    }

    private void toUpperCase(){
        if(houseNo !=null) houseNo = houseNo.toUpperCase();
        if(location !=null) location = location.toUpperCase();
        if(description !=null) description = description.toUpperCase();
    }

}
