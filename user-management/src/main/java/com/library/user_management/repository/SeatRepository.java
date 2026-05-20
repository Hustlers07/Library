package com.library.user_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.user_management.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long>{

    Optional<Seat> findBySeatId(String upperCase);

}
