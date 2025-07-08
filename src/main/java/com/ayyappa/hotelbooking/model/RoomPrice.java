package com.ayyappa.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
    name = "room_prices",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_duration", columnNames = {"room_id", "duration_type"})
    },
    indexes = {
        @Index(name = "idx_price_room", columnList = "room_id"),
        @Index(name = "idx_price_duration", columnList = "duration_type")
    }
)
public class RoomPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    private Room room;

    @Column(name = "duration_type", nullable = false, length = 20)
    private String durationType; // "24_HOURS", "12_HOURS", etc.

    @Column(name = "price", nullable = false)
    private Double price;
}
