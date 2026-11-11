package com.mygym.app.outbox;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "outbox_events")
@CompoundIndex(name = "pending_idx", def = "{'publishedAt': 1, 'createdAt': 1}")
public record OutboxEvent(
        @Id String id,
        String aggregateType,
        String aggregateId,
        String topic,
        String messageKey,
        BroadcastPayload payload,
        Instant createdAt,
        Instant lockedUntil,
        Integer attempts,
        @Indexed(expireAfter = "7d", sparse = true) Instant publishedAt) {
}
