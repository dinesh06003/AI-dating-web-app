package com.dinesh.ai_dating_backend.repo;

import com.dinesh.ai_dating_backend.model.Profile;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends MongoRepository<Profile, String> {

    @Aggregation(pipeline = {"{$sample: {size: 1}}"})
    Profile getRandomProfile();

}
