package com.dinesh.ai_dating_backend.model;

public record Match(
        String Id,
        Profile profile,
        String conversationId
) {
}
