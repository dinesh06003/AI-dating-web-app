package com.dinesh.tinder_with_ai_backend.Repo;

import com.dinesh.tinder_with_ai_backend.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepo extends MongoRepository<Conversation, String> {
}
