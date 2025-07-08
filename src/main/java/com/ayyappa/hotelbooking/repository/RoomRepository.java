package com.ayyappa.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayyappa.hotelbooking.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByHotel_Id(Long hotelId);
    
    @Query("""
    SELECT r FROM Room r
    WHERE r.hotel.id = :hotelId
      AND r.id NOT IN :bookedRoomIds
    """)
    List<Room> findAvailableRoomsByHotelIdAndNotInRoomIds(
    @Param("hotelId") Long hotelId,
    @Param("bookedRoomIds") List<Long> bookedRoomIds
    );
    List<Room> findRoomIdsByHotel_Id(Long hotelId); // Fallback if no rooms booked
}
