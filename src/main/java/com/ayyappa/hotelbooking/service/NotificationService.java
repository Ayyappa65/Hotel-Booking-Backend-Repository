package com.ayyappa.hotelbooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ayyappa.hotelbooking.model.Notification;
import com.ayyappa.hotelbooking.model.User;
import com.ayyappa.hotelbooking.repository.NotificationRepository;
import com.ayyappa.hotelbooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    // @Autowired
    // private SimpMessagingTemplate messagingTemplate;

    // public Notification savenotification(Notification notification) {
    // Notification saved = notificationRepository.save(notification);

    // // Push via WebSocket to user
    // messagingTemplate.convertAndSendToUser(
    //     saved.getUserId().toString(),
    //     "/queue/notifications",
    //     saved
    //     );
 
    //     return saved;
    // }

    /**
     * Save a new notification.
     */
    public void  saveNotification(Notification notification) {
        log.info("Saving notification for userId: {}", notification.getUserId());
        notificationRepository.save(notification);
    }


    /**
     * Get unread notifications for a specific userby descnding order.
     */
    public List<Notification> getUnreadNotificationsByUser(Long userId) {
        log.info("Fetching unread notifications for userId: {}", userId);
        return notificationRepository.findByUserIdAndReadFalseOrderByTimestampDesc(userId);
    }

    /**
     * Mark a notification as read.
     */
    public boolean markNotificationAsRead(List<Long> notificationIds) {
        log.info("Marking notification as read: ID={}", notificationIds);
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);

        if (notifications.isEmpty()) {
            log.warn("No notifications found for IDs: {}", notificationIds);
            return false;
        }

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
        return true;
    }

    /**
     * Send a broadcast notification to all user IDs.
     */
    public void broadcastNotification(Notification templateNotification) {
        log.info("Broadcasting notification to users");
        // Fetch all user IDs from user repository
        List<Long> userIds = userRepository.findAll()
                                        .stream()
                                        .map(User::getId) // Replace 'getId' if your method name differs
                                        .collect(Collectors.toList());
        for (Long userId : userIds) {
            Notification copy = new Notification(
                templateNotification.getMessage(),
                templateNotification.getType(),
                userId,
                templateNotification.getTimestamp().toString(),
                templateNotification.getPriority()
            );
            notificationRepository.save(copy);
        }
    }
}
