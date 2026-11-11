package com.mygym.app.controller;

import static com.mygym.app.util.ResourceCrudConstants.ID_PLACEHOLDER_ENDPOINT;
import static com.mygym.app.util.ResourceCrudConstants.NOTIFICATION_CRUD_REST_BASE;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mygym.app.models.Notification;
import com.mygym.app.service.NotificationCrudService;
import com.mygym.app.validation.OnCreate;

@RestController
@RequestMapping(NOTIFICATION_CRUD_REST_BASE)
public class NotificationCrudController {

    private final NotificationCrudService service;

    public NotificationCrudController(NotificationCrudService service) {
        this.service = service;
    }

    @PostMapping
    public Notification create(@Validated({Default.class, OnCreate.class}) @RequestBody Notification notification) {
        return service.createNotification(notification);
    }

    @GetMapping
    public List<Notification> readAll() {
        return service.getAllNotifications();
    }

    @GetMapping(ID_PLACEHOLDER_ENDPOINT)
    public Notification readById(@PathVariable String id) {
        return service.getNotificationById(id);
    }

    @PutMapping(ID_PLACEHOLDER_ENDPOINT)
    public Notification update(@PathVariable String id, @Valid @RequestBody Notification notification) {
        return service.updateNotification(id, notification);
    }

    @DeleteMapping(ID_PLACEHOLDER_ENDPOINT)
    public String delete(@PathVariable String id) {
        service.deleteNotification(id);
        return "Notification deleted successfully with id: " + id;
    }
}
