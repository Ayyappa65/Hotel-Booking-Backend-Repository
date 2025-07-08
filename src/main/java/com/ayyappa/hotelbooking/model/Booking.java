package com.ayyappa.hotelbooking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(
    name = "bookings",
    indexes = {
        @Index(name = "idx_booking_user", columnList = "user_id"),
        @Index(name = "idx_booking_room", columnList = "room_id"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_check_in_out", columnList = "check_in_time, check_out_time")
    }
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time", nullable = false)
    private LocalDateTime checkOutTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // Consider: Enum (BOOKED, CANCELLED, COMPLETED)

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "duration_type", nullable = false, length = 20)
    private String durationType; // Consider: Enum (24_HOURS, etc.)

    @Column(name = "guest_count")
    private Integer guestCount;

    @Column(name = "special_requests", length = 1000)
    private String specialRequests;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus; // Consider: Enum (PAID, UNPAID, PARTIAL)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
