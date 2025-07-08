package com.ayyappa.hotelbooking.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for successful login.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}
