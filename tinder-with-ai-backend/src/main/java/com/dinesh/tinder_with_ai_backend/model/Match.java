package com.dinesh.tinder_with_ai_backend.model;

public record Match(
        String Id,
        Profile profile,
        String conversationId
) {
}
