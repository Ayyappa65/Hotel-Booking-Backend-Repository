package com.ayyappa.hotelbooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayyappa.hotelbooking.model.Notification;
import com.ayyappa.hotelbooking.payload.response.MessageResponse;
import com.ayyappa.hotelbooking.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * save notifiction for single user.
     */
    @PostMapping
    public ResponseEntity<MessageResponse> createNotification(@RequestBody Notification notification) {
        log.debug("Creating notification for user: {}", notification.getUserId());
        notificationService.saveNotification(notification);
        return ResponseEntity.ok(new MessageResponse("Notification Sent Successfully!"));
    }

    /**
     * Get all unread notifications for a user.
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    /**
     * Mark a notifications as read by ids.
     */
    @PutMapping
    public ResponseEntity<MessageResponse> markNotificationAsRead(@RequestBody List<Long> notificationIds) {
        boolean success = notificationService.markNotificationAsRead(notificationIds);
        if (success) {
            return ResponseEntity.ok(new MessageResponse("Notification marked as read."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Broadcast a notification to all users.
     * Replace hardcoded IDs with DB call in real app.
     */
    @PostMapping("/broadcast")
    public ResponseEntity<MessageResponse> broadcastNotification(@RequestBody Notification notification) {
        notificationService.broadcastNotification(notification);
        return ResponseEntity.ok(new MessageResponse("Broadcast sent to all users."));
    }
}
