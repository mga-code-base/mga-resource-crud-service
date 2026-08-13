package com.mygym.app.controller;

import com.mygym.app.models.Notification;
import com.mygym.app.service.NotificationCrudService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCrudControllerTest {

    @Mock
    private NotificationCrudService service;

    @InjectMocks
    private NotificationCrudController controller;

    @Test
    @DisplayName("Should create notification entity")
    void testCreate_Success() {
        Notification notification = new Notification();
        when(service.createNotification(any(Notification.class))).thenReturn(notification);

        Notification result = controller.create(notification);

        assertNotNull(result);
        verify(service, times(1)).createNotification(notification);
    }

    @Test
    @DisplayName("Should read all notifications")
    void testReadAll_Success() {
        when(service.getAllNotifications()).thenReturn(List.of(new Notification()));

        List<Notification> result = controller.readAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should delete notification by ID")
    void testDelete_Success() {
        doNothing().when(service).deleteNotification("N-901");

        String result = controller.delete("N-901");

        assertEquals("Notification deleted successfully with id: N-901", result);
        verify(service, times(1)).deleteNotification("N-901");
    }
}