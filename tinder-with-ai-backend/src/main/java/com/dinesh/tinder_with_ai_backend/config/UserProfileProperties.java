package com.dinesh.tinder_with_ai_backend.config;


import com.dinesh.tinder_with_ai_backend.model.Gender;
import com.dinesh.tinder_with_ai_backend.model.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tinderai.character.user")
public class UserProfileProperties {
    private String id;
    private String firstName;
    private String lastName;
    private int age;
    private String ethnicity;
    private String gender;
    private String bio;
    private String imageUrl;
    private String myersBriggsPersonalityType;

    public Profile toProfile() {
        return new Profile(
                id, firstName, lastName, age,
                ethnicity, gender !=null ? Gender.valueOf(gender): Gender.MALE,
                bio, imageUrl, myersBriggsPersonalityType
        );
    }
}
