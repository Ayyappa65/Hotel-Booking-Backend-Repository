package com.ayyappa.hotelbooking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayyappa.hotelbooking.payload.request.SendMailRequest;
import com.ayyappa.hotelbooking.payload.response.MessageResponse;
import com.ayyappa.hotelbooking.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    /**
     * Endpoint to send an email using provided request details.
     * Logs the request and status of the email operation.
     *
     * @param sendMail the email request payload containing recipient, subject, and body
     * @return success message
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendEmail(@Valid @RequestBody SendMailRequest sendMail) {
        String toEmail = sendMail.getToEmail();
        String subject = sendMail.getSubject();
        String body = sendMail.getBody();

        logger.info("Received request to send email to: {}", toEmail);

        try {
            emailService.sendBookingConfirmation(toEmail, subject, body);
            logger.info("Email successfully sent to: {}", toEmail);
            return ResponseEntity.ok(new MessageResponse("Email sent successfully to " + toEmail));
        } catch (Exception e) {
            logger.error("Failed to send email to: {}. Error: {}", toEmail, e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Failed to send email to " + toEmail));
        }
    }
}
