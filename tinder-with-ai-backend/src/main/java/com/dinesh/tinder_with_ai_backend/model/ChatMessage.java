package com.dinesh.tinder_with_ai_backend.model;

import java.time.LocalDateTime;

public record ChatMessage(String messageText,
                          String authorId,
                          LocalDateTime messageTime) {
}
