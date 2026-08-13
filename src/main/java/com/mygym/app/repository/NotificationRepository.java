package com.mygym.app.repository;

import com.mygym.app.models.Notification;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

	Optional<Notification> findByNotificationId(String notificationId);
}
