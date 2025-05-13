package com.dinesh.ai_dating_backend.controller;


import com.dinesh.ai_dating_backend.repo.ProfileRepo;
import com.dinesh.ai_dating_backend.model.Profile;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileRepo profileRepo;

    public ProfileController(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }


    @GetMapping("/random")
    public Profile getRamdonProfile(){

         Profile profile = profileRepo.getRandomProfile();
         return profile;
    }
}
