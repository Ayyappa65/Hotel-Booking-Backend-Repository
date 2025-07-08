package com.ayyappa.hotelbooking.controller;

import com.ayyappa.hotelbooking.dto.HotelDTO;
import com.ayyappa.hotelbooking.payload.response.MessageResponse;
import com.ayyappa.hotelbooking.dto.HotelDTO.HotelResponseDTO;
import com.ayyappa.hotelbooking.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing hotels.
 */
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN','USER','MANAGER')")
public class HotelController {

    private final HotelService hotelService;

    /**
     * Create a new hotel.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageResponse> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        log.info("API - Create hotel: {}", hotelDTO.getName());
       hotelService.createHotel(hotelDTO);
        return ResponseEntity.ok(new MessageResponse("Data Adeed Successfully!"));
    }

    /**
     * Get list of all hotels.
     */
    @GetMapping
    public ResponseEntity<List<HotelResponseDTO>> getAllHotels() {
        log.info("API - Get all hotels");
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    /**
     * Update hotel by ID.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping
    public ResponseEntity<MessageResponse> updateHotel(@Valid @RequestBody HotelDTO.HotelResponseDTO hotelDTO) {
        log.info("API - Update hotel ID: {}", hotelDTO.getId());
        hotelService.updateHotel(hotelDTO.getId(), hotelDTO);
        return ResponseEntity.ok(new MessageResponse("Data updated Successfully!"));
    }

    /**
     * Delete hotel by ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteHotel(@PathVariable Long id) {
        log.info("API - Delete hotel ID: {}", id);
        hotelService.deleteHotel(id);
        return ResponseEntity.ok(new MessageResponse("Hotel deleted successfully"));
    }
}
