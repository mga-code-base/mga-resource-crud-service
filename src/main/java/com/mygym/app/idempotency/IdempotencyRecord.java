package com.mygym.app.idempotency;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "idempotency_keys")
public record IdempotencyRecord(
        @Id String id,
        String requestHash,
        Status status,
        Integer responseStatus,
        String contentType,
        byte[] responseBody,
        @Indexed(expireAfter = "24h") Instant createdAt) {

    public enum Status {
        IN_PROGRESS, COMPLETED
    }

    public static IdempotencyRecord started(String id, String requestHash, Instant now) {
        return new IdempotencyRecord(id, requestHash, Status.IN_PROGRESS, null, null, null, now);
    }

    public IdempotencyRecord completed(int responseStatus, String contentType, byte[] responseBody) {
        return new IdempotencyRecord(id, requestHash, Status.COMPLETED, responseStatus, contentType, responseBody, createdAt);
    }
}
