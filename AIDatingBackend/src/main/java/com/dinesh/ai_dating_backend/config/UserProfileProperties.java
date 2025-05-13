package com.dinesh.ai_dating_backend.config;


import com.dinesh.ai_dating_backend.model.Gender;
import com.dinesh.ai_dating_backend.model.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "character.user")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMyersBriggsPersonalityType() {
        return myersBriggsPersonalityType;
    }

    public void setMyersBriggsPersonalityType(String myersBriggsPersonalityType) {
        this.myersBriggsPersonalityType = myersBriggsPersonalityType;
    }

    public Profile toProfile() {
        Profile myProfile = new Profile(
                id, firstName, lastName, age,
                ethnicity, gender !=null ? Gender.valueOf(gender): Gender.MALE,
                bio, imageUrl, myersBriggsPersonalityType
        );
        System.out.println("Creating my profile: " + myProfile);
        return myProfile;
    }
}
