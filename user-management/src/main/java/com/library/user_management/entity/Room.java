package com.library.user_management.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoomStatus status =RoomStatus.ROOM_VACANT;

    @Column(nullable=false)
    private String location;

    @Column(nullable=false)
    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Seat> seats;

    @ManyToMany(mappedBy = "rooms", cascade = CascadeType.ALL)
    private List<User> user;

}
