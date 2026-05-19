package com.library.user_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.user_management.service.RoomServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RoomController
 * REST API endpoints for room management and allocation.
 */
@Slf4j
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "APIs for room management.")
public class RoomController {

    private final RoomServiceImpl roomService;
}
