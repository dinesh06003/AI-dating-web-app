package com.dinesh.ai_dating_backend.controller;


import com.dinesh.ai_dating_backend.repo.ConversationRepo;
import com.dinesh.ai_dating_backend.repo.MatchRepo;
import com.dinesh.ai_dating_backend.repo.ProfileRepo;
import com.dinesh.ai_dating_backend.model.Conversation;
import com.dinesh.ai_dating_backend.model.Match;
import com.dinesh.ai_dating_backend.model.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
public class MatchController {

    private final ConversationRepo conversationRepo;

    private final MatchRepo matchRepo;
    private final ProfileRepo profileRepo;

    public MatchController(ConversationRepo conversationRepo, MatchRepo matchRepo, ProfileRepo profileRepo) {
        this.conversationRepo = conversationRepo;
        this.matchRepo = matchRepo;
        this.profileRepo = profileRepo;
    }

    public record CreateMatchRequest(String profileId){

    }

    @PostMapping("/matches")
    public Match createMatch(@RequestBody CreateMatchRequest request){
        System.out.println("==========Creating Match=======");

        Profile profile = profileRepo.findById(request.profileId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "unable to find a profile with ID: " + request.profileId));
        System.out.println("Profile : " + profile);
        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                profile.id(),
                new ArrayList<>()
        );
        System.out.println("Conversation : " + conversation);

        conversationRepo.save(conversation);
        Match match = new Match(UUID.randomUUID().toString(),
                profile,
                conversation.id());
        matchRepo.save(match);
        System.out.println("Match : " + match);
        return match;
    }


    @GetMapping("/matches")
    public List<Match> getAllMatches(){
        System.out.println("fetching Matches : " + matchRepo.findAll());
        return matchRepo.findAll();
    }
}
