package com.dinesh.tinder_with_ai_backend.Repo;

import com.dinesh.tinder_with_ai_backend.model.Profile;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends MongoRepository<Profile, String> {

    @Aggregation(pipeline = {"{$sample: {size: 1}}"})
    Profile getRandomProfile();

}
