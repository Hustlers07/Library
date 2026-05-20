package com.library.user_management.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.library.user_management.dto.RoomRequest;
import com.library.user_management.entity.Room;
import com.library.user_management.repository.RoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl {

    private final RoomRepository roomRepository;

    /**
     * Creates a new room entity
     * 
     * @param location
     * @param description
     */
    public Room create(RoomRequest roomRequest) {

        boolean exists = roomRepository.existsByHouseNoAndFloorAndLocation(
                roomRequest.getHouseNo(), roomRequest.getFloor(), roomRequest.getLocation());
        if (exists) {
            throw new IllegalArgumentException("Room with same houseNo, floor, and location already exists");
        }

        Room room = Room.builder()
                .houseNo(roomRequest.getHouseNo())
                .floor(roomRequest.getFloor())
                .location(roomRequest.getLocation())
                .description(roomRequest.getDescription())
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("New room created with location: " + savedRoom);

        return savedRoom;
    }

    public Room update(Long rid, RoomRequest roomRequest) throws Exception {

        Optional<Room> optRoom = roomRepository.findById(rid);

        if (!optRoom.isPresent())
            throw new Exception("Room not found exception");

        Room room = optRoom.get();
        room.setHouseNo(roomRequest.getHouseNo());
        room.setFloor(roomRequest.getFloor());
        room.setLocation(roomRequest.getLocation());
        room.setDescription(roomRequest.getDescription());

        Room updatedRoom = roomRepository.save(room);
        log.info("New room created with location: " + updatedRoom);

        return updatedRoom;
    }

    public Optional<Room> getRoomById(long roomId){
       return roomRepository.findById(roomId);
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }
}
