package com.ayyappa.hotelbooking.service;

import com.ayyappa.hotelbooking.dto.HotelDTO;
import com.ayyappa.hotelbooking.dto.HotelDTO.HotelResponseDTO;

import java.util.List;

public interface HotelService {
    void createHotel(HotelDTO hotelDTO);
    List<HotelResponseDTO> getAllHotels();
    HotelResponseDTO getHotelById(Long id);
    void updateHotel(Long id, HotelDTO.HotelResponseDTO hotelDTO);
    void deleteHotel(Long id);
}
