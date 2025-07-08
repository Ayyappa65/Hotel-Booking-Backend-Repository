The notification system has been updated to persist notifications in the database, ensuring that users can retrieve missed notifications when they reconnect. Here's how it works:

1. Storage: All notifications are now stored in the database using JPA/Hibernate
Notifications are saved before being sent via WebSocket
Each notification includes: message, type, emuserId, timestamp, and priority
Retrieval: When a user connects:

2. The client calls fetchMissedNotifications() before establishing the WebSocket connection
This fetches any notifications the user missed while disconnected
Retrieved notifications are displayed using the same callback as real-time notifications
Cleanup: Users can clear their notification history using the DELETE endpoint

3. Unread Notification Retrieval
GET /api/v1/notificationsuser/{userId}/unread
Returns unread notifications for the user.
Sorted by timestamp DESC

4. Mark as Read
PUT /api/v1/notificationsuser
Accepts a JSON array of notification IDs like [12, 234, 45].
Marks them as read in DB.

5. Best Practices Followed
LocalDateTime used for timestamps 
DB persistence occurs before WebSocket delivery 
Ordered notifications (recent first) 
Stateless client reconnect logic with fetch fallback 

To test the implementation:
----------------------------
Start the application
Login as an employee
Disconnect the client (close tab/logout)
Send notifications to the employee
Reconnect - you should see all notifications that were sent while disconnected