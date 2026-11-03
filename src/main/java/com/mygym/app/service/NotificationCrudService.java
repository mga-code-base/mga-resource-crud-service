package com.mygym.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mygym.app.models.Notification;
import com.mygym.app.repository.NotificationRepository;

@Service
public class NotificationCrudService {

    private final NotificationRepository repository;
    private final IdGenerationService idGenerationService;

    public NotificationCrudService(NotificationRepository repository, IdGenerationService idGenerationService) {
        this.repository = repository;
        this.idGenerationService = idGenerationService;
    }

    public Notification createNotification(Notification notification) {
        return repository.save(notification.withNotificationId(idGenerationService.nextNotificationId()));
    }

    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public Notification getNotificationById(String id) {
        return repository.findByNotificationId(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
    }

    public Notification updateNotification(String id, Notification updatedData) {
        Notification existing = getNotificationById(id);
        return repository.save(new Notification(existing.notificationId(), updatedData.messageType(),
                updatedData.message(), updatedData.image(), updatedData.generatingAdmin(), existing.publishStatusCheck()));
    }

    public void deleteNotification(String id) {
        repository.delete(getNotificationById(id));
    }
}
