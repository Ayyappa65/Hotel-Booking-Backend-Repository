package com.ayyappa.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayyappa.hotelbooking.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications by user
    List<Notification> findByUserId(Long userId);

    // Get all unread notifications by user
    List<Notification> findByUserIdAndReadFalseOrderByTimestampDesc(Long userId);

}
