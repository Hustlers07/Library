package com.library.user_management.service;

import java.util.stream.IntStream;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.user_management.repository.SeatRepository;
import com.library.user_management.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import com.library.user_management.entity.Room;
import com.library.user_management.entity.RoomStatus;
import com.library.user_management.entity.Seat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SeatServiceImpl {

    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final RoomServiceImpl roomServiceImpl;

    public List<Seat> addSeats(long roomId, int count) {

        Optional<Room> room = roomServiceImpl.getRoomById(roomId);

        if (!room.isPresent())
            throw new EntityNotFoundException("Room not found");

        Room selectedRoom = room.get();

        if (selectedRoom.getStatus() == RoomStatus.ROOM_VACANT ||
                (selectedRoom.getStatus() == RoomStatus.ROOM_OCCUPIED && (selectedRoom.getUser() == null || selectedRoom.getUser().isEmpty()))) {

                    if(selectedRoom.getSeats().size() > count-1)
                        throw new IllegalArgumentException("Count should be greater than existing seat count");
 
                    List<Seat> newSeats = IntStream.rangeClosed(selectedRoom.getSeats().size()+1, count)
                    .mapToObj(val -> Seat.builder()
                            .room(selectedRoom)
                            .seatId(val + "")
                            .build())
                    .toList();

            selectedRoom.setStatus(RoomStatus.ROOM_OCCUPIED);
            selectedRoom.getSeats().addAll(newSeats);
            List<Seat> addedSeats = seatRepository.saveAll(newSeats);
            return addedSeats;
        } else {
            throw new IllegalArgumentException("Selected room is not available for seating.");
        }
    }

    public Seat disable(String seatId) throws BadRequestException{
       Optional<Seat> seat = seatRepository.findBySeatId(seatId.toUpperCase());

       if(!seat.isPresent())
            throw new BadRequestException("Seat id is incorrect");

       Seat selectedSeat = seat.get();
       selectedSeat.setActive(false);

       return seatRepository.save(selectedSeat);
    }

    public Seat addOrUpdateUser(String userName, String seatId){

        if(!userRepository.existsByUsername(userName))
            throw new IllegalArgumentException("Invalid user.");

        Optional<Seat> seat = seatRepository.findBySeatId(seatId);
  
        if(!seat.isPresent())
            throw new IllegalArgumentException("Invalid seatId.");
        Seat currentSeat = seat.get();
        currentSeat.setUsers(userRepository.findByUsername(userName).get());

        return seatRepository.save(currentSeat);
    }

}
