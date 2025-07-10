package com.ayyappa.hotelbooking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
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

    // Map to hold locks for each room to prevent concurrent booking conflicts
    private final ConcurrentHashMap<Long, ReentrantLock> roomLockMap = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Create or update a booking based on whether the ID is present.
     * Also checks if the room is available in the given time slot.
     */
    @Override
    public BookingDTO saveOrUpdateBooking(BookingDTO dto) {
        Long roomId = dto.getRoomId();

        // it works only single-instance Spring Boot apps. But...
        // If you scale horizontally (multiple server instances), this won’t prevent race conditions across instances.
        //Solution for Distributed Systems (Optional Now):
        //Use database-level locking (@Lock with PESSIMISTIC_WRITE).
        //Or use a distributed lock system like Redis Redlock, Zookeeper, or Hazelcast.
        ReentrantLock lock = roomLockMap.computeIfAbsent(roomId, rid -> new ReentrantLock());

        //This helps trace deadlocks or waiting threads in a high-load environment.
        log.debug("Trying to acquire lock for roomId {}", roomId);
        lock.lock();
        log.debug("Lock acquired for roomId {}", roomId);

        try {

            boolean isUpdate = dto.getId() != null;
            log.info("{} booking request received for roomId={}, userId={}", 
                isUpdate ? "Update" : "Create", dto.getRoomId(), dto.getUserId());

            // Check room availability only for create or time change during update
            if (!isUpdate || isBookingTimeChanged(dto)) {
                List<Booking> conflicts = bookingRepository.findConflictingBookingsExcludingId(
                                                    dto.getRoomId(),
                                                    dto.getCheckInTime(),
                                                    dto.getCheckOutTime(),
                                                    isUpdate ? dto.getId() : null
                );
                if (!conflicts.isEmpty()) {
                    log.warn("Room {} is already booked between {} and {}. Conflicting bookings: {}",
                        dto.getRoomId(), dto.getCheckInTime(), dto.getCheckOutTime(),
                        conflicts.stream().map(b -> b.getId().toString()).collect(Collectors.joining(", "))
                        );
                    throw new ResourceNotFound("Room is not available during the selected time.");
                }
            }

            Booking booking;
            if (isUpdate) {
                booking = bookingRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFound("Booking not found"));
            } else {
                booking = new Booking();
                Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFound("Room not found"));
                User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFound("User not found"));
                booking.setRoom(room);
                booking.setUser(user);
            }

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
        } finally {
            lock.unlock();
        }
    }

    /**
    * Checks whether the check-in or check-out datetime has changed.
    * Used only during an update request.
    *
    * @param dto BookingDTO containing new date-time range
    * @return true if booking time has changed
    */
    private boolean isBookingTimeChanged(BookingDTO dto) {
        if (dto.getId() == null) return true; // New booking → assume time changed

        return bookingRepository.findById(dto.getId())
        .map(existing -> {
            LocalDateTime existingStart = existing.getCheckInTime();
            LocalDateTime existingEnd = existing.getCheckOutTime();
            LocalDateTime newStart = dto.getCheckInTime();
            LocalDateTime newEnd = dto.getCheckOutTime();

            boolean changed = !(existingStart.equals(newStart) && existingEnd.equals(newEnd));

            if (changed) {
                log.info("Booking time changed for booking ID {}: [{} → {}] to [{} → {}]",
                        dto.getId(), existingStart, existingEnd, newStart, newEnd);
            } else {
                log.debug("Booking time NOT changed for booking ID {}", dto.getId());
            }
            return changed;
        })
        .orElse(true); // Booking not found → treat as changed
    }


    /*
     * check availability rooms by with check-in and check-out time
     */
    @Override
    public Map<Long, Boolean> checkRoomAvailabilityParallel(List<Long> roomIds, LocalDateTime checkIn, LocalDateTime checkOut) {
        // Step 1: Single DB call
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookingsForRooms(roomIds, checkIn, checkOut);

        // Step 2: Identify rooms with conflicts
        Set<Long> roomsWithConflicts = conflictingBookings.parallelStream()
            .map(b -> b.getRoom().getId())
            .collect(Collectors.toSet());

        // Step 3: Build availability map
        return roomIds.parallelStream()
            .collect(Collectors.toConcurrentMap(
                roomId -> roomId,
                roomId -> !roomsWithConflicts.contains(roomId)
            ));
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

    /*
    private final RedissonClient redissonClient; // <-- Inject this


    @Override
    public BookingDTO saveOrUpdateBooking(BookingDTO dto) {
    Long roomId = dto.getRoomId();

    // ✅ Generate a unique distributed lock key per room
    String lockKey = "booking-lock-room-" + roomId;
    RLock lock = redissonClient.getLock(lockKey);

    log.debug("Attempting to acquire distributed lock for roomId: {}", roomId);

    boolean acquired = false;
    try {
        // ✅ Try to acquire the lock within 10 seconds, and auto-release it after 30 seconds
        acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);

        if (!acquired) {
            log.warn("Failed to acquire lock for roomId {} within timeout", roomId);
            throw new IllegalStateException("Room is currently being booked. Please try again shortly.");
        }

        log.info("Distributed lock acquired for roomId {}", roomId);

        boolean isUpdate = dto.getId() != null;
        log.info("{} booking request received for roomId={}, userId={}", 
            isUpdate ? "Update" : "Create", dto.getRoomId(), dto.getUserId());

        // ✅ Check for conflicting bookings only if it's a new booking or the time has changed
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

        // ✅ If it's an update, fetch the existing booking
        if (isUpdate) {
            booking = bookingRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFound("Booking not found"));
        } else {
            // ✅ Else, create a new booking and fetch Room and User entities
            booking = new Booking();
            Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFound("Room not found"));
            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFound("User not found"));
            booking.setRoom(room);
            booking.setUser(user);
        }

        // ✅ Set or update booking fields from DTO
        booking.setCheckInTime(dto.getCheckInTime());
        booking.setCheckOutTime(dto.getCheckOutTime());
        booking.setStatus(dto.getStatus());
        booking.setTotalAmount(dto.getTotalAmount());
        booking.setDurationType(dto.getDurationType());
        booking.setGuestCount(dto.getGuestCount());
        booking.setSpecialRequests(dto.getSpecialRequests());
        booking.setPaymentStatus(dto.getPaymentStatus());

        // ✅ Save the booking
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking {} successfully with ID: {}", 
            isUpdate ? "updated" : "created", savedBooking.getId());

        return mapToDTO(savedBooking);

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Interrupted while trying to acquire lock for roomId: {}", roomId, e);
        throw new IllegalStateException("Request interrupted. Please try again.");
    } finally {
        if (acquired && lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("Lock released for roomId {}", roomId);
        }
    }
    }
   */
}
