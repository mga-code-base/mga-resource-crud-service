package com.mygym.app.outbox;

import java.time.Clock;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mygym.app.models.Notification;

@Component
public class OutboxWriter {

    private final MongoTemplate mongoTemplate;
    private final String broadcastTopic;
    private final Clock clock;

    @Autowired
    public OutboxWriter(MongoTemplate mongoTemplate, @Value("${mga.outbox.broadcast-topic:broadcast-messages}") String broadcastTopic) {
        this(mongoTemplate, broadcastTopic, Clock.systemUTC());
    }

    OutboxWriter(MongoTemplate mongoTemplate, String broadcastTopic, Clock clock) {
        this.mongoTemplate = mongoTemplate;
        this.broadcastTopic = broadcastTopic;
        this.clock = clock;
    }

    public OutboxEvent notificationCreated(Notification notification) {
        OutboxEvent event = new OutboxEvent(
                UUID.randomUUID().toString(),
                "notification",
                notification.notificationId(),
                broadcastTopic,
                notification.generatingAdmin(),
                new BroadcastPayload(notification.generatingAdmin(), notification.message(), notification.image()),
                clock.instant(),
                null,
                0,
                null);
        return mongoTemplate.insert(event);
    }
}
