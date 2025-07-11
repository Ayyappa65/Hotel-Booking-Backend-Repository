package com.ayyappa.hotelbooking.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMailRequest {

    @NotBlank(message = "Recipient email must not be blank")
    @Email(message = "Invalid email format")
    private String toEmail;

    @NotBlank(message = "Subject must not be blank")
    private String subject;

    @NotBlank(message = "Body must not be blank")
    private String body;
}