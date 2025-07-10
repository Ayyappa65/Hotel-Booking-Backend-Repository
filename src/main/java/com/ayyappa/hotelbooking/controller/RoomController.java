package com.ayyappa.hotelbooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayyappa.hotelbooking.dto.RoomDTO;
import com.ayyappa.hotelbooking.payload.response.MessageResponse;
import com.ayyappa.hotelbooking.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing rooms.
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN','USER','MANAGER','RECEPTIONIST')")
public class RoomController {

    private final RoomService roomService;

    /**
     * Create a new room.
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<MessageResponse> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        log.info("API - Create room: {}", roomDTO.getRoomNumber());
        roomService.createRoom(roomDTO);
        return ResponseEntity.ok(new MessageResponse("Data added successfully!"));
    }

    /**
     * Create a new room.
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping
    public ResponseEntity<MessageResponse> updateRoom(@Valid @RequestBody RoomDTO roomDTO) {
        log.info("API - Update room: {}", roomDTO.getRoomNumber());
        roomService.updateRoom(roomDTO.getId(), roomDTO);
        return ResponseEntity.ok(new MessageResponse("Data updated successfully!"));
    }

    /**
     * Get room by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        log.info("API - Get room by ID: {}", id);
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all rooms for a specific hotel.
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelId(@PathVariable Long hotelId) {
        log.info("API - Get rooms for hotel ID: {}", hotelId);
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }

    // @PostMapping("/available")
    // public ResponseEntity<List<RoomDTO>> getAvailableRooms(@RequestBody RoomDTO.FetchAvailableByHostelIdAndDate dto) {

    //     log.info("API - Get available rooms for hotelId={} between {} and {}", dto.getHotelId(), dto.getCheckIn(), dto.getCheckOut());
    //     List<RoomDTO> rooms = roomService.getAvailableRooms( dto.getHotelId(), dto.getCheckIn(), dto.getCheckOut());
    //     return ResponseEntity.ok(rooms);
    // }

    /**
     * Delete a room by ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRoom(@PathVariable Long id) {
        log.info("API - Delete room ID: {}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.ok(new MessageResponse("Room deleted successfully!"));
    }
}
