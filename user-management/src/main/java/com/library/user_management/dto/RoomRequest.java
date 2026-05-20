package com.library.user_management.dto;

import com.library.user_management.entity.Floor;
import lombok.*;

/**
 * DTO request for Room
 */

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    private String houseNo;
    private Floor floor;
    private String location;
    private String description;
}
