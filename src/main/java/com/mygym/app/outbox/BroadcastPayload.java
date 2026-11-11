package com.mygym.app.outbox;

public record BroadcastPayload(String senderId, String content, String image) {
}
