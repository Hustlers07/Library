package com.library.user_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.user_management.dto.SeatRequest;
import com.library.user_management.entity.Seat;
import com.library.user_management.service.SeatServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/seat")
@RequiredArgsConstructor
@Tag(name = "Seat Management", description = "APIs for seat management")
public class SeatController {

    private final SeatServiceImpl seatService;

    @PostMapping("/add")
    @Operation(summary = "Add seat to vacant room", description = "Add seat to seat entity if a vacant room is available.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
  
    public ResponseEntity<?> addSeats(@RequestBody SeatRequest seatRequest){

        List<Seat> seats = seatService.addSeats(seatRequest.getRoomId(), seatRequest.getSeatCount());

        return ResponseEntity.status(HttpStatus.CREATED).body(seats);
    }

}
