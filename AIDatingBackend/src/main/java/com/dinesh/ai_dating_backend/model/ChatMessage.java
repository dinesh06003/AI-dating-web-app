package com.dinesh.ai_dating_backend.model;

import java.time.LocalDateTime;

public record ChatMessage(String messageText,
                          String authorId,
                          LocalDateTime messageTime) {
}
