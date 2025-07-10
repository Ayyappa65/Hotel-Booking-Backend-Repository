package com.ayyappa.hotelbooking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayyappa.hotelbooking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
    SELECT b FROM Booking b
    WHERE b.room.id = :roomId
      AND b.status IN ('BOOKED', 'COMPLETED')
      AND (:excludeId IS NULL OR b.id != :excludeId)
      AND (
          (:checkInTime BETWEEN b.checkInTime AND b.checkOutTime)
          OR (:checkOutTime BETWEEN b.checkInTime AND b.checkOutTime)
          OR (b.checkInTime BETWEEN :checkInTime AND :checkOutTime)
      )
    """)
    List<Booking> findConflictingBookingsExcludingId(
        @Param("roomId") Long roomId,
        @Param("checkInTime") LocalDateTime checkInTime,
        @Param("checkOutTime") LocalDateTime checkOutTime,
        @Param("excludeId") Long excludeId
    );



    @Query("""
    SELECT b.room.id FROM Booking b
    WHERE b.room.hotel.id = :hotelId
      AND b.status IN ('BOOKED', 'COMPLETED')
      AND (
           (:checkInTime BETWEEN b.checkInTime AND b.checkOutTime) OR
           (:checkOutTime BETWEEN b.checkInTime AND b.checkOutTime) OR
           (b.checkInTime BETWEEN :checkInTime AND :checkOutTime)
      )
    """)
    List<Long> findBookedRoomIdsByHotelAndTime(
        @Param("hotelId") Long hotelId,
        @Param("checkInTime") LocalDateTime checkInTime,
        @Param("checkOutTime") LocalDateTime checkOutTime
    );

    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByUserId(Long userId);
}
