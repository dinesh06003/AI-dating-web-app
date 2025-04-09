package com.dinesh.tinder_with_ai_backend;

import com.dinesh.tinder_with_ai_backend.Repo.ConversationRepo;
import com.dinesh.tinder_with_ai_backend.Repo.MatchRepo;
import com.dinesh.tinder_with_ai_backend.Repo.ProfileRepo;
import com.dinesh.tinder_with_ai_backend.service.ProfileCreationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TinderWithAiBackendApplication implements CommandLineRunner {

	private final ProfileRepo profileRepo;

	private final ConversationRepo conversationRepo;

    private final MatchRepo matchRepo;

    private final ProfileCreationService profileCreationService;

    public TinderWithAiBackendApplication(ProfileRepo profileRepo, ConversationRepo conversationRepo, MatchRepo matchRepo, ProfileCreationService profileCreationService) {
        this.profileRepo = profileRepo;
        this.conversationRepo = conversationRepo;
        this.matchRepo = matchRepo;
        this.profileCreationService = profileCreationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(TinderWithAiBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        clearAllData();
        profileCreationService.createProfiles(10);
        profileCreationService.saveProfilesToDB();



    }

    private void clearAllData() {
        conversationRepo.deleteAll();
        matchRepo.deleteAll();
        profileRepo.deleteAll();
        System.out.println("==========Deleted Conversation data, Matches data, Profiles data before loading data ===========");

    }
}
