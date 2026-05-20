package com.library.user_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.user_management.entity.Floor;
import com.library.user_management.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{

    boolean existsByHouseNoAndFloorAndLocation(String houseNo, Floor floor, String location);

}
