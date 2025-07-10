package com.ayyappa.hotelbooking.controller;

import java.util.List;
import java.util.Map;

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

import com.ayyappa.hotelbooking.dto.BookingDTO;
import com.ayyappa.hotelbooking.payload.request.CheckAvailabilityRooms;
import com.ayyappa.hotelbooking.payload.response.MessageResponse;
import com.ayyappa.hotelbooking.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing room bookings.
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Create a new booking.
     */
    @PostMapping
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody BookingDTO dto) {
        log.info("API - Create booking: User ID = {}, Room ID = {}", dto.getUserId(), dto.getRoomId());
        bookingService.saveOrUpdateBooking(dto);
        return ResponseEntity.ok(new MessageResponse("Booking created successfully!"));
    }

    /**
     * Get all bookings by room id.
     */
    @GetMapping("/room/{id}")
    public ResponseEntity<List<BookingDTO>> getAllBookingsByRoomId(@PathVariable Long id) {
        log.info("API - Get all bookings by room ID: {}",id);
        return ResponseEntity.ok(bookingService.getAllBookingsByRoomId(id));
    }

    /**
     * Get all bookings by user id.
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<BookingDTO>> getAllBookingsByUserId(@PathVariable Long id) {
        log.info("API - Get all bookings by user ID: {}",id);
        return ResponseEntity.ok(bookingService.getAllBookingsByUserId(id));
    }

    /**
     * Get a specific booking by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getOne(@PathVariable Long id) {
        log.info("API - Get booking by ID: {}", id);
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a booking by ID.
     */
    @PutMapping
    public ResponseEntity<MessageResponse> update(@Valid @RequestBody BookingDTO dto) {
        log.info("API - Update booking ID: {}", dto.getId());
        bookingService.saveOrUpdateBooking(dto);
        return ResponseEntity.ok(new MessageResponse("Booking updated successfully!"));
    }

    /*
     * check-rooms availability by room ids and time
     */
    @PostMapping("/check-availability")
    public ResponseEntity<Map<Long, Boolean>> checkAvailability(@RequestBody CheckAvailabilityRooms availability) {
        log.info("Checking availability rooms {} between {} and {}",availability.getRoomIds(), availability.getCheckIn(), availability.getCheckOut());
        Map<Long, Boolean> result = bookingService.checkRoomAvailabilityParallel(availability.getRoomIds(), availability.getCheckIn(), availability.getCheckOut());
        return ResponseEntity.ok(result);
    }

    /**
     * Delete a booking by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        log.warn("API - Delete booking ID: {}", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(new MessageResponse("Booking deleted successfully!"));
    }
}
