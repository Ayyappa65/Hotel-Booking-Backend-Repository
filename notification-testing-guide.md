Notification System Testing Guide — Hotel Booking Application

Objective
To test the end-to-end functionality of the notification system — including real-time delivery, persistence, retrieval of missed messages, and marking messages as read — for both private and broadcast notifications.

🧪 1. Test Setup Checklist
Item	                      Status
Backend server running	      ✅
WebSocket /ws enabled         ✅
Frontend/client ready	      ✅
User account (e.g., ID=1)     ✅



🚀 2. Real-Time Notification (Private)
✅ Steps:
you can send notification to private uuser in two ways via Rest or WebSocket
       Via Rest
    ------------
1. Start backend
2. Open client with WebSocket connection:
3. Ensure the frontend connects and subscribes to /user/{userId}/queue/notifications
4. Send private notification:
    Use REST: POST /api/v1/notifications
    Body:
{
  "userId": 1,
  "message": "Your room has been upgraded!",
  "type": "booking",
  "priority": "HIGH",
  "timestamp": "2025-07-08T15:00:00"
}

        via WebSocket 
    ------------------
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


5. Expected result:
Notification is stored in DB
Client receives it instantly via WebSocket
UI displays: Your room has been upgraded!


🌐 3. Broadcast Notification
✅ Steps:
you can send notification to all users in two ways via Rest or WebSocket
       Via Rest
    ------------
1. All clients subscribed to /topic/notifications
2. Send broadcast message via REST
3. REST endpoint:
POST /api/v1/notificationsuser/broadcast
Body:
Edit
{
  "message": "Room 204 is now available!",
  "type": "availability",
  "priority": "HIGH",
  "timestamp": "2025-07-08T15:10:00"
}
       via WebSocket 
    ------------------
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

4. Expected result:
Notification is sent to all users via REST
Stored in DB for each user
Visible on UI immediately



🔌 4. Missed Notification Retrieval
✅ Steps:
1. Open app, login as userId=1
2. Disconnect (close tab/logout)
3. While user is offline, send notification:
{
  "userId": 1,
  "message": "Check-in time updated!",
  "type": "alert",
  "priority": "LOW",
  "timestamp": "2025-07-08T15:20:00"
}
4. Reconnect client
5. Client should call: GET /api/v1/notificationsuser/1/unread
6. Expected result:
Missed notification is returned
UI displays it along with real-time messages



✅ 5. Mark Notifications as Read
✅ Steps:
1. Assume unread notification IDs: [12, 14, 18]
2. Call: PUT /api/v1/notificationsuser
   BODY 
   [12, 14, 18]
3. Expected result:
These notifications are updated in DB with read = true
They are no longer returned in the unread API



🗑️ 6. Clear Notification History (Optional)
✅ Steps:
1. Call: DELETE /api/v1/notificationsuser/1
2. Expected result:
Notifications for user 1 are deleted or soft-deleted from DB