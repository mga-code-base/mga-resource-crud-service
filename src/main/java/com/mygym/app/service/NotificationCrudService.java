package com.mygym.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mygym.app.models.Notification;
import com.mygym.app.outbox.OutboxWriter;
import com.mygym.app.repository.NotificationRepository;
import com.mygym.app.web.NotFoundException;

@Service
public class NotificationCrudService {

    private final NotificationRepository repository;
    private final IdGenerationService idGenerationService;
    private final OutboxWriter outbox;

    public NotificationCrudService(NotificationRepository repository, IdGenerationService idGenerationService,
            OutboxWriter outbox) {
        this.repository = repository;
        this.idGenerationService = idGenerationService;
        this.outbox = outbox;
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        Notification saved = repository.save(notification
                .withNotificationId(idGenerationService.nextNotificationId())
                .withPublishStatusCheck(Boolean.FALSE));
        outbox.notificationCreated(saved);
        return saved;
    }

    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public Notification getNotificationById(String id) {
        return repository.findByNotificationId(id)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
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
