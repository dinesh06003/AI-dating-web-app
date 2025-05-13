package com.dinesh.ai_dating_backend.repo;

import com.dinesh.ai_dating_backend.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepo extends MongoRepository<Conversation, String> {
}
