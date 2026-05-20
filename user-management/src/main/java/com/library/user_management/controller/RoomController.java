package com.library.user_management.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.user_management.dto.RoomRequest;
import com.library.user_management.entity.Room;
import com.library.user_management.service.RoomServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RoomController
 * REST API endpoints for room management and allocation.
 */
@Slf4j
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "APIs for room management.")
public class RoomController {

    private final RoomServiceImpl roomService;


    /**
     * Fetches all rooms available
     * @return List of rooms
     */

    @GetMapping("/all")
    @Operation(summary = "Fetches all the rooms", description = "Fetches list of rooms present in db")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?>  getAllRooms(){
        return ResponseEntity.ok(roomService.getAll());
    }

    /**
     * Creates a new room
     * POST /api/room/create
     * @param request the room request
     * @return created room object
     */
    @PostMapping("/create")
    @Operation(summary = "Creates a new room", description = "Creates a new room entity")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest request){

        log.info("Creating room : ", request);
        
        try{
            Room room = roomService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch(Exception ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Update an existing room
     * POST /api/room/update/{iroomId}
     * @param roomId id of the room to update
     * @param request the room request
     * @return updates an existing room object
     */
    @PostMapping("/update/{roomId}")
    @Operation(summary = "Updates an existing room", description = "Updates an existing room entity")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId, @RequestBody RoomRequest request){

        log.info("Updating room details for id: {] with req: {} ", request);
        
        try{
            Room room = roomService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch(Exception ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", ex.getMessage()));
        }
    }

}
