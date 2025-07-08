package com.ayyappa.hotelbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayyappa.hotelbooking.model.RoomPrice;

public interface RoomPriceRepository extends JpaRepository<RoomPrice, Long> {

    // Get all prices for a specific room
    List<RoomPrice> findByRoom_Id(Long roomId);

    // Get price by room and duration type
    Optional<RoomPrice> findByRoom_IdAndDurationType(Long roomId, String durationType);

    // Check if a specific duration price exists for a room
    boolean existsByRoom_IdAndDurationType(Long roomId, String durationType);

    // Delete all prices for a specific room (optional cleanup)
    void deleteByRoom_Id(Long roomId);
}
