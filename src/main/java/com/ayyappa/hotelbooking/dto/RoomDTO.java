package com.ayyappa.hotelbooking.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class RoomDTO {

    private Long id;
    private String roomNumber;
    private String type;
    private Boolean available;
    private Integer floorNumber;
    private Integer bedCount;
    private Boolean isAc;
    private String description;
    private String imageUrl;
    private Integer maxOccupancy;
    private Long hotelId;

    private List<RoomPriceDTO> prices;

    @Data
    public static class FetchAvailableByHostelIdAndDate{
        private Long hotelId;
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
    }
}
