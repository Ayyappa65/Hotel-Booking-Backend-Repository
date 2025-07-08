package com.ayyappa.hotelbooking.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.ayyappa.hotelbooking.model.Notification;
import com.ayyappa.hotelbooking.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationService notificationService;

    /**
     * Broadcast message to all connected clients on /topic/notifications
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/notifications")
    public Notification sendToAll(Notification notification) {
        log.info("Broadcasting message: {}", notification.getMessage());

        // Save broadcast to DB for each user
        notificationService.broadcastNotification(notification); // save for all users

        return notification;
    }

    /**
     * Send private message to a specific user on /user/{userId}/queue/notifications
     */
    @MessageMapping("/private")
    public void sendToUser(Notification notification) {
        Long userId = notification.getUserId();
        if (userId != null) {
            log.info("Sending private message to userId: {}", userId);

            // Save to DB
            notificationService.saveNotification(notification); // persist before sending

            messagingTemplate.convertAndSendToUser(
                userId.toString(),            // destination user identifier
                "/queue/notifications",       // destination suffix
                notification                  // actual payload
            );
        } else {
            log.warn("Notification userId is null");
        }
    }
}
