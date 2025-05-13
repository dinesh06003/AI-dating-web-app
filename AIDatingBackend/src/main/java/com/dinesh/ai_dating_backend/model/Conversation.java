package com.dinesh.ai_dating_backend.model;

import java.util.List;

public record Conversation(String id,
                           String profileId,
                           List<ChatMessage> messages) {
}
