package com.ayyappa.hotelbooking.service.impl;

import com.ayyappa.hotelbooking.dto.HotelDTO;
import com.ayyappa.hotelbooking.dto.HotelDTO.HotelResponseDTO;
import com.ayyappa.hotelbooking.exception.ResourceNotFound;
import com.ayyappa.hotelbooking.model.Hotel;
import com.ayyappa.hotelbooking.repository.HotelRepository;
import com.ayyappa.hotelbooking.service.HotelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation that contains business logic for managing hotels.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    /**
     * Creates and saves a new hotel entity in the database.
     * 
     * @param hotelDTO the DTO containing hotel details to be created
     */
    @Override
    public void createHotel(HotelDTO hotelDTO) {
        log.info("Creating hotel: {}", hotelDTO.getName());
        hotelRepository.save(toEntity(hotelDTO));
    }

    /**
     * Retrieves all hotels from the database and maps them to response DTOs.
     * 
     * @return a list of HotelResponseDTO
     */
    @Override
    public List<HotelResponseDTO> getAllHotels() {
        log.info("Fetching all hotels");
        return hotelRepository.findAll().stream()
                .map(HotelServiceImpl::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds a hotel by its ID.
     * 
     * @param id the ID of the hotel to retrieve
     * @return the corresponding HotelResponseDTO
     * @throws ResourceNotFound if no hotel is found with the given ID
     */
    @Override
    public HotelResponseDTO getHotelById(Long id) {
        log.info("Fetching hotel by ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Hotel not found with ID: " + id));
        return toDTO(hotel);
    }

    /**
     * Updates an existing hotel by its ID.
     * 
     * @param id the ID of the hotel to update
     * @param hotelDTO the DTO containing updated hotel information
     * @throws ResourceNotFound if no hotel is found with the given ID
     */
    @Override
    public void updateHotel(Long id, HotelDTO.HotelResponseDTO hotelDTO) {
        log.info("Updating hotel with ID: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Hotel not found with ID: " + id));

        // Update hotel fields with new values
        hotel.setName(hotelDTO.getName());
        hotel.setCity(hotelDTO.getCity());
        hotel.setState(hotelDTO.getState());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setPincode(hotelDTO.getPincode());
        hotel.setPhoneNumber(hotelDTO.getPhoneNumber());
        hotel.setEmail(hotelDTO.getEmail());
        hotel.setDescription(hotelDTO.getDescription());
        hotel.setRating(hotelDTO.getRating());

        hotelRepository.save(hotel);
    }

    /**
     * Deletes a hotel by its ID.
     * 
     * @param id the ID of the hotel to delete
     * @throws ResourceNotFound if no hotel is found with the given ID
     */
    @Override
    public void deleteHotel(Long id) {
        log.info("Deleting hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Hotel not found with ID: " + id));
        hotelRepository.delete(hotel);
    }

    /**
     * Converts HotelDTO to Hotel entity.
     * 
     * @param dto the DTO to convert
     * @return Hotel entity
     */
    private static Hotel toEntity(HotelDTO dto) {
        return Hotel.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .state(dto.getState())
                .address(dto.getAddress())
                .pincode(dto.getPincode())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .description(dto.getDescription())
                .rating(dto.getRating())
                .build();
    }

    /**
     * Converts Hotel entity to HotelResponseDTO.
     * 
     * @param hotel the Hotel entity to convert
     * @return HotelResponseDTO
     */
    private static HotelResponseDTO toDTO(Hotel hotel) {
        return HotelResponseDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .state(hotel.getState())
                .address(hotel.getAddress())
                .pincode(hotel.getPincode())
                .phoneNumber(hotel.getPhoneNumber())
                .email(hotel.getEmail())
                .description(hotel.getDescription())
                .rating(hotel.getRating())
                .build();
    }
}
