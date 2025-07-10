package com.ayyappa.hotelbooking.payload.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckAvailabilityRooms {

    @NotBlank(message= "please give room ids")
    private List<Long> roomIds;

   @NotBlank(message= "please give check in time")
    private LocalDateTime checkIn;

    @NotBlank(message= "please give check out time")
    private LocalDateTime checkOut;
}
