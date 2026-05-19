package com.library.user_management.dto;

import com.library.user_management.entity.Floor;

import lombok.*;

/**
 * DTO request for Room
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    @NonNull String houseNo;
    @NonNull Floor floor;
    @NonNull String location;
    String description;

}
