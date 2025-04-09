package com.dinesh.tinder_with_ai_backend.controller;


import com.dinesh.tinder_with_ai_backend.Repo.ProfileRepo;
import com.dinesh.tinder_with_ai_backend.model.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api")
public class ProfileController {

    private final ProfileRepo profileRepo;

    public ProfileController(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/profiles/random")
    public Profile getRamdonProfile(){

         Profile profile = profileRepo.getRandomProfile();
         return profile;
    }
}
