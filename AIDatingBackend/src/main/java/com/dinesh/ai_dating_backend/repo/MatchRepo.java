package com.dinesh.ai_dating_backend.repo;

import com.dinesh.ai_dating_backend.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepo extends MongoRepository<Match, String> {
}
