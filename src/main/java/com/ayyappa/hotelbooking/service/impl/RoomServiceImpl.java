package com.ayyappa.hotelbooking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ayyappa.hotelbooking.dto.RoomDTO;
import com.ayyappa.hotelbooking.dto.RoomPriceDTO;
import com.ayyappa.hotelbooking.exception.ResourceNotFound;
import com.ayyappa.hotelbooking.model.Hotel;
import com.ayyappa.hotelbooking.model.Room;
import com.ayyappa.hotelbooking.model.RoomPrice;
import com.ayyappa.hotelbooking.repository.BookingRepository;
import com.ayyappa.hotelbooking.repository.HotelRepository;
import com.ayyappa.hotelbooking.repository.RoomRepository;
import com.ayyappa.hotelbooking.service.RoomService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, HotelRepository hotelRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Creates a new room and saves it to the database
     */
    @Override
    public void createRoom(RoomDTO dto) {
        log.info("Creating new room: {}", dto.getRoomNumber());

        Room room = mapToEntity(dto);
        roomRepository.save(room);

        log.info("Room created successfully with ID: {}", room.getId());
    }

    @Override
public void updateRoom(Long id, RoomDTO dto) {
    log.info("Updating room with ID: {}", id);

    Room existingRoom = roomRepository.findById(id).orElseThrow(() -> {
        log.error("Room not found with ID: {}", id);
        return new ResourceNotFound("Room not found");
    });

    // Update basic fields
    existingRoom.setRoomNumber(dto.getRoomNumber());
    existingRoom.setType(dto.getType());
    existingRoom.setAvailable(dto.getAvailable());
    existingRoom.setFloorNumber(dto.getFloorNumber());
    existingRoom.setBedCount(dto.getBedCount());
    existingRoom.setIsAc(dto.getIsAc());
    existingRoom.setDescription(dto.getDescription());
    existingRoom.setImageUrl(dto.getImageUrl());
    existingRoom.setMaxOccupancy(dto.getMaxOccupancy());

    // Update hotel reference if changed
    if (!existingRoom.getHotel().getId().equals(dto.getHotelId())) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> {
                    log.error("Hotel not found with ID: {}", dto.getHotelId());
                    return new ResourceNotFound("Hotel not found");
                });
        existingRoom.setHotel(hotel);
    }

    // Replace old prices with new prices
    if (dto.getPrices() != null) {
        List<RoomPrice> updatedPrices = dto.getPrices().stream().map(priceDTO -> {
            RoomPrice price = new RoomPrice();
            price.setId(priceDTO.getId()); // Preserve ID for updates if available
            price.setDurationType(priceDTO.getDurationType());
            price.setPrice(priceDTO.getPrice());
            price.setRoom(existingRoom);
            return price;
        }).collect(Collectors.toList());

        // Clear and replace the price list
        existingRoom.getPrices().clear();
        existingRoom.getPrices().addAll(updatedPrices);
    }

    roomRepository.save(existingRoom);
    log.info("Room updated successfully with ID: {}", id);
}


    /**
     * Fetch a room by its ID
     */
    @Override
    public Optional<RoomDTO> getRoomById(Long id) {
        log.info("Fetching room with ID: {}", id);
        return roomRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Fetch all rooms belonging to a specific hotel
     */
    @Override
    public List<RoomDTO> getRoomsByHotelId(Long hotelId) {
        log.info("Fetching all rooms for hotel ID: {}", hotelId);

        return roomRepository.findByHotel_Id(hotelId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
    * Fetches all available rooms for a specific hotel within the given check-in and check-out time.
    */
    @Override
    public List<RoomDTO> getAvailableRooms(Long hotelId, LocalDateTime checkIn, LocalDateTime checkOut) {
           log.info("Checking available rooms for hotel ID: {} from {} to {}", hotelId, checkIn, checkOut);
           List<Long> bookedRoomIds = bookingRepository.findBookedRoomIdsByHotelAndTime(hotelId, checkIn, checkOut);

            List<Room> availableRooms;
        if (bookedRoomIds.isEmpty()) {
            log.info("No bookings found. Returning all rooms for hotel ID: {}", hotelId);
            availableRooms = roomRepository.findByHotel_Id(hotelId);
        } else {
            log.info("Excluding {} booked rooms for the given time slot", bookedRoomIds.size());
            availableRooms = roomRepository.findAvailableRoomsByHotelIdAndNotInRoomIds(hotelId, bookedRoomIds);
        }
        return availableRooms.stream()
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
    }


    /**
     * Delete a room by its ID
     */
    @Override
    public void deleteRoom(Long id) {
        log.warn("Deleting room with ID: {}", id);
        roomRepository.deleteById(id);
    }

    /**
     * Convert Room entity to DTO for response
     */
    private RoomDTO mapToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setType(room.getType());
        dto.setAvailable(room.getAvailable());
        dto.setFloorNumber(room.getFloorNumber());
        dto.setBedCount(room.getBedCount());
        dto.setIsAc(room.getIsAc());
        dto.setDescription(room.getDescription());
        dto.setImageUrl(room.getImageUrl());
        dto.setMaxOccupancy(room.getMaxOccupancy());
        dto.setHotelId(room.getHotel().getId());

        // Map prices for different durations (24hr, 12hr, etc.)
        if (room.getPrices() != null) {
            dto.setPrices(room.getPrices().stream().map(price -> {
                RoomPriceDTO priceDTO = new RoomPriceDTO();
                priceDTO.setId(price.getId());
                priceDTO.setDurationType(price.getDurationType());
                priceDTO.setPrice(price.getPrice());
                return priceDTO;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Convert RoomDTO to entity for saving to database
     */
    private Room mapToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setType(dto.getType());
        room.setAvailable(dto.getAvailable());
        room.setFloorNumber(dto.getFloorNumber());
        room.setBedCount(dto.getBedCount());
        room.setIsAc(dto.getIsAc());
        room.setDescription(dto.getDescription());
        room.setImageUrl(dto.getImageUrl());
        room.setMaxOccupancy(dto.getMaxOccupancy());

        // Fetch the hotel entity by ID
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> {
                    log.error("Hotel not found with ID: {}", dto.getHotelId());
                    return new ResourceNotFound("Hotel not found");
                });
        room.setHotel(hotel);

        // Map room prices for various duration types
        if (dto.getPrices() != null) {
            List<RoomPrice> prices = dto.getPrices().stream().map(priceDTO -> {
                RoomPrice price = new RoomPrice();
                price.setDurationType(priceDTO.getDurationType());
                price.setPrice(priceDTO.getPrice());
                price.setRoom(room); // maintain bidirectional mapping
                return price;
            }).collect(Collectors.toList());
            room.setPrices(prices);
        }

        return room;
    }
}
