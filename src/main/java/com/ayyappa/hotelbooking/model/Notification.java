package com.ayyappa.hotelbooking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "user_id") 
    private Long userId;

    @Column(name = "timestamp", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime timestamp;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean read = false;

    // Constructor for simple messages (updated to use userId)
    public Notification(String message, String type, Long userId, String timestamp, String priority) {
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.timestamp = LocalDateTime.parse(timestamp);
        this.priority = priority;
    }
}
