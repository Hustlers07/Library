package com.library.user_management.service;

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
     * @param location
     * @param description
     */
    public Room create(RoomRequest roomRequest){

        Room room = Room.builder()
        .houseNo(roomRequest.getHouseNo())
        .floor(roomRequest.getFloor())
        .location(roomRequest.getLocation())
        .description(roomRequest.getDescription())
        .build();

        Room savedRoom = roomRepository.save(room);
        log.info("New room created with location: "+savedRoom);

        return savedRoom;
    }
}
