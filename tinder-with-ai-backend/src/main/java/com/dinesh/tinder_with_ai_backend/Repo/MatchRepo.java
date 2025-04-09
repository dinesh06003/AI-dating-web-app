package com.dinesh.tinder_with_ai_backend.Repo;

import com.dinesh.tinder_with_ai_backend.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepo extends MongoRepository<Match, String> {
}
