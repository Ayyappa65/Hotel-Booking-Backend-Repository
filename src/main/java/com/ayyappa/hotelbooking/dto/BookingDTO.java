package com.ayyappa.hotelbooking.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
    private Double totalAmount;
    private String durationType;
    private Integer guestCount;
    private String specialRequests;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}
