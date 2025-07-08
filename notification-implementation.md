🏨 Notification System Implementation Guide — Hotel Booking Application

Overview
The notification system is implemented to send real-time alerts to users (e.g., users or customers) regarding booking confirmations, room availability, and other events. It uses WebSocket with STOMP over SockJS for instant communication and ensures persistence via a relational database so that users can retrieve missed notifications on reconnect.

⚙️ Components Added

🔧 NotificationService

Responsible for:
Storing notifications in the database.
Sending messages over WebSocket via SimpMessagingTemplate.

Supports:
Sending private messages to a specific user.
Broadcasting messages to all users.
Includes error handling so notification failures don’t affect booking or business logic.



🔌 WebSocket Configuration
Configured at: /ws endpoint
Uses SockJS with STOMP protocol

Message broker destinations:
/user/{userId}/queue/notifications → For private messages
/topic/notifications → For global broadcast messages
Supports reconnection and fallback via SockJS

🧠 Usage Example (Client-side JavaScript)
const userId = 1; // Replace with actual logged-in user ID
// Connect to WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Subscribe to user-specific and broadcast notifications
stompClient.connect({}, function (frame) {
    // Private channel
    stompClient.subscribe('/user/' + userId + '/queue/notifications', function (notification) {
        const data = JSON.parse(notification.body);
        console.log('[Private]', data.message);
        // Show as toast or notification in UI
    });

    // Global channel
    stompClient.subscribe('/topic/notifications', function (notification) {
        const data = JSON.parse(notification.body);
        console.log('[Broadcast]', data.message);
        // Show as toast or banner
    });
});



🔄 Missed Notification Retrieval (on reconnect)
Flow:
When user (customer/user) reconnects:
Call: GET /api/v1/notificationsuser/{userId}/unread
Display each missed notification using the same handler as WebSocket.



✅ Testing the Notification System
You can test the full system by:

1. Send private notification -> Use WebSocket /app/private
2. Send broadcast notification -> Use WebSocket /app/broadcast

3. Disconnect client (e.g., logout or close tab)
4. Send notifications during disconnection

5. Reconnect App will call GET /api/v1/notificationsuser/{userId}/unread endpoint and display pending messages


6. Mark notifications as read
   PUT /api/v1/notificationsuser with body: [1, 2, 3]
