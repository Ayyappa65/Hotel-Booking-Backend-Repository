package com.ayyappa.hotelbooking.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ayyappa.hotelbooking.dto.BookingDTO;
import com.ayyappa.hotelbooking.exception.ResourceNotFound;
import com.ayyappa.hotelbooking.model.Booking;
import com.ayyappa.hotelbooking.model.Room;
import com.ayyappa.hotelbooking.model.User;
import com.ayyappa.hotelbooking.repository.BookingRepository;
import com.ayyappa.hotelbooking.repository.RoomRepository;
import com.ayyappa.hotelbooking.repository.UserRepository;
import com.ayyappa.hotelbooking.service.BookingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for booking operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    /**
     * Create or update a booking based on whether the ID is present.
     * Also checks if the room is available in the given time slot.
     */
    @Override
    public BookingDTO saveOrUpdateBooking(BookingDTO dto) {
        boolean isUpdate = dto.getId() != null;
        log.info("{} booking request received for roomId={}, userId={}", 
                 isUpdate ? "Update" : "Create", dto.getRoomId(), dto.getUserId());

        // Step 1: Check room availability only for create or time change during update
        if (!isUpdate || isBookingTimeChanged(dto)) {
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                dto.getRoomId(), dto.getCheckInTime(), dto.getCheckOutTime());

            if (!conflicts.isEmpty()) {
                log.warn("Room {} is already booked between {} and {}",
                         dto.getRoomId(), dto.getCheckInTime(), dto.getCheckOutTime());
                throw new IllegalStateException("Room is not available during the selected time.");
            }
        }

        Booking booking;

        if (isUpdate) {
            // Fetch and update existing booking
            booking = bookingRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Booking not found with ID: {}", dto.getId());
                    return new ResourceNotFound("Booking not found");
                });
        } else {
            // Create new booking
            booking = new Booking();

            Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> {
                    log.error("Room not found with ID: {}", dto.getRoomId());
                    return new ResourceNotFound("Room not found");
                });

            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", dto.getUserId());
                    return new ResourceNotFound("User not found");
                });

            booking.setRoom(room);
            booking.setUser(user);
        }

        // Set booking data
        booking.setCheckInTime(dto.getCheckInTime());
        booking.setCheckOutTime(dto.getCheckOutTime());
        booking.setStatus(dto.getStatus());
        booking.setTotalAmount(dto.getTotalAmount());
        booking.setDurationType(dto.getDurationType());
        booking.setGuestCount(dto.getGuestCount());
        booking.setSpecialRequests(dto.getSpecialRequests());
        booking.setPaymentStatus(dto.getPaymentStatus());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} successfully with ID: {}", 
                 isUpdate ? "updated" : "created", savedBooking.getId());

        return mapToDTO(savedBooking);
    }

    /**
     * Check if check-in or check-out time has been modified in update request.
     */
    private boolean isBookingTimeChanged(BookingDTO dto) {
        if (dto.getId() == null) return true;

        return bookingRepository.findById(dto.getId())
            .map(existing -> !existing.getCheckInTime().equals(dto.getCheckInTime())
                          || !existing.getCheckOutTime().equals(dto.getCheckOutTime()))
            .orElse(true);
    }

    /**
     * Get all bookings by Room ID.
     */
    @Override
    public List<BookingDTO> getAllBookingsByRoomId(Long id) {
        log.info("Fetching all bookings for room ID: {}", id);
        return bookingRepository.findByRoomId(id).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all bookings by User ID.
     */
    @Override
    public List<BookingDTO> getAllBookingsByUserId(Long id) {
        log.info("Fetching all bookings for user ID: {}", id);
        return bookingRepository.findByUserId(id).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a single booking by booking ID.
     */
    @Override
    public Optional<BookingDTO> getBookingById(Long id) {
        log.info("Fetching booking by ID: {}", id);
        return bookingRepository.findById(id)
                .map(this::mapToDTO);
    }

    /**
     * Delete a booking by ID.
     */
    @Override
    public void deleteBooking(Long id) {
        log.warn("Deleting booking with ID: {}", id);
        bookingRepository.deleteById(id);
    }

    /**
     * Convert Booking entity to BookingDTO.
     */
    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setCheckInTime(booking.getCheckInTime());
        dto.setCheckOutTime(booking.getCheckOutTime());
        dto.setStatus(booking.getStatus());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setDurationType(booking.getDurationType());
        dto.setGuestCount(booking.getGuestCount());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setLastUpdatedAt(booking.getLastUpdatedAt());
        return dto;
    }
}
