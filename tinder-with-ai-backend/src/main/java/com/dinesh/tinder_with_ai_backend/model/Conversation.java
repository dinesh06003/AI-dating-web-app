package com.dinesh.tinder_with_ai_backend.model;

import java.util.List;

public record Conversation(String id,
                           String profileId,
                           List<ChatMessage> messages) {
}
