package com.library.user_management.dto;

import com.library.user_management.entity.Floor;

import lombok.*;

/**
 * DTO response for Room
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    long roomId;
    String houseNo;
    Floor floor;
    String location;
    String description;
}
