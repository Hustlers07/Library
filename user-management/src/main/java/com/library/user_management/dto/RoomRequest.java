package com.library.user_management.dto;

import com.library.user_management.entity.Floor;
import com.library.user_management.entity.Room;

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
    public Room findById(Long rid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}
