package com.ayyappa.hotelbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class HotelDTO {

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Address is required")
    private String address;

    private String pincode;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number should be valid")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String description;

    private Double rating;

    @Data
    @Builder
    public static class HotelResponseDTO{
        private Long id;
        private String name;
        private String city;
        private String state;
        private String address;
        private String pincode;
        private String phoneNumber;
        private String email;
        private String description;
        private Double rating;
    }
}
