package com.ayyappa.hotelbooking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for sending emails related to hotel booking operations.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a booking confirmation email to the specified recipient.
     *
     * @param toEmail Recipient email address.
     * @param subject Email subject line.
     * @param body    Email body content.
     */
    public void sendBookingConfirmation(String toEmail, String subject, String body) {
        logger.info("Preparing to send booking confirmation email to: {}", toEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("youremail@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
