package com.ayyappa.hotelbooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ayyappa.hotelbooking.dto.RoomDTO;

public interface RoomService {
    void createRoom(RoomDTO roomDTO);
    void updateRoom(Long id, RoomDTO dto) ;
    Optional<RoomDTO> getRoomById(Long id);
    List<RoomDTO> getRoomsByHotelId(Long hotelId);
    List<RoomDTO> getAvailableRooms(Long hotelId, LocalDateTime checkIn, LocalDateTime checkOut);
    void deleteRoom(Long id);
}
