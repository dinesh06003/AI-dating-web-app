package com.dinesh.ai_dating_backend;

import com.dinesh.ai_dating_backend.repo.ConversationRepo;
import com.dinesh.ai_dating_backend.repo.MatchRepo;
import com.dinesh.ai_dating_backend.repo.ProfileRepo;
import com.dinesh.ai_dating_backend.service.ProfileCreationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AIDatingBackendApplication implements CommandLineRunner {

	private final ProfileRepo profileRepo;

	private final ConversationRepo conversationRepo;

    private final MatchRepo matchRepo;

    private final ProfileCreationService profileCreationService;

    public AIDatingBackendApplication(ProfileRepo profileRepo, ConversationRepo conversationRepo, MatchRepo matchRepo, ProfileCreationService profileCreationService) {
        this.profileRepo = profileRepo;
        this.conversationRepo = conversationRepo;
        this.matchRepo = matchRepo;
        this.profileCreationService = profileCreationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AIDatingBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        clearAllData();
        profileCreationService.createProfiles(0);
        profileCreationService.saveProfilesToDB();

    }

    private void clearAllData() {
        conversationRepo.deleteAll();
        matchRepo.deleteAll();
        profileRepo.deleteAll();
        System.out.println("==========Deleted Conversation data, Matches data, Profiles data before loading data ===========");

    }
}
