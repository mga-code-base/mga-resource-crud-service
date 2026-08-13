package com.mygym.app.service;

import com.mygym.app.models.Notification;
import com.mygym.app.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service managing broadcast system notification entities and admin message logs.
 */
@Service
public class NotificationCrudService {

    private final NotificationRepository repository;
    private final IdGenerationService service;
    
    public NotificationCrudService(NotificationRepository repository, IdGenerationService service) {
        this.service = service;
        this.repository = repository;
    }

    public Notification createNotification(Notification notification) {
        notification.setNotificationId(String.valueOf(service.getNotificationId()));
        service.incrementNotificationId();
        return repository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public Notification getNotificationById(String id) {
        return repository.findByNotificationId(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    public Notification updateNotification(String id, Notification updatedData) {
        Notification existing = getNotificationById(id);
        existing.setMessageType(updatedData.getMessageType());
        existing.setMessage(updatedData.getMessage());
        existing.setImage(updatedData.getImage());
        existing.setGeneratingAdmin(updatedData.getGeneratingAdmin());
        return repository.save(existing);
    }

    public void deleteNotification(String id) {
        Notification existing = getNotificationById(id);
        repository.delete(existing);
    }
}