package com.ayyappa.hotelbooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ayyappa.hotelbooking.dto.BookingDTO;

public interface BookingService {
    BookingDTO saveOrUpdateBooking(BookingDTO bookingDTO);
    Map<Long, Boolean> checkRoomAvailabilityParallel(List<Long> roomIds, LocalDateTime checkIn, LocalDateTime checkOut);
    List<BookingDTO> getAllBookingsByRoomId(Long id);
    List<BookingDTO> getAllBookingsByUserId(Long id);
    Optional<BookingDTO> getBookingById(Long id);
    void deleteBooking(Long id);
}
