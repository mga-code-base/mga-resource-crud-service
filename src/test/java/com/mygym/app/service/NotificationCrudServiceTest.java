package com.mygym.app.service;

import com.mygym.app.models.Notification;
import com.mygym.app.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCrudServiceTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private IdGenerationService idGenerationService;

    @InjectMocks
    private NotificationCrudService notificationCrudService;

    @Test
    @DisplayName("Should create notification and assign ID sequence")
    void testCreateNotification_Success() {
        Notification notification = new Notification(null, null, "Gym closed on holiday", null, null, null);

        when(idGenerationService.nextNotificationId()).thenReturn("901");
        when(repository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = notificationCrudService.createNotification(notification);

        assertNotNull(result);
        assertEquals("901", result.notificationId());
    }

    @Test
    @DisplayName("Should fetch notification by ID")
    void testGetNotificationById_Success() {
        Notification notification = new Notification("N-901", null, null, null, null, null);

        when(repository.findByNotificationId("N-901")).thenReturn(Optional.of(notification));

        Notification result = notificationCrudService.getNotificationById("N-901");

        assertNotNull(result);
        assertEquals("N-901", result.notificationId());
    }
}