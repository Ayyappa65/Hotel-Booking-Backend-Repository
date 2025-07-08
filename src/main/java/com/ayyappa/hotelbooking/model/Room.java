package com.ayyappa.hotelbooking.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
    name = "rooms",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_number_hotel", columnNames = {"room_number", "hotel_id"})
    },
    indexes = {
        @Index(name = "idx_room_hotel", columnList = "hotel_id"),
        @Index(name = "idx_room_type", columnList = "type"),
        @Index(name = "idx_room_availability", columnList = "available")
    }
)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "bed_count")
    private Integer bedCount;

    @Column(name = "is_ac")
    private Boolean isAc;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RoomPrice> prices;
}
