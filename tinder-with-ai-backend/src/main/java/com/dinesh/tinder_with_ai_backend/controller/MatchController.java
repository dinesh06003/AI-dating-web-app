package com.dinesh.tinder_with_ai_backend.controller;


import com.dinesh.tinder_with_ai_backend.Repo.ConversationRepo;
import com.dinesh.tinder_with_ai_backend.Repo.MatchRepo;
import com.dinesh.tinder_with_ai_backend.Repo.ProfileRepo;
import com.dinesh.tinder_with_ai_backend.model.Conversation;
import com.dinesh.tinder_with_ai_backend.model.Match;
import com.dinesh.tinder_with_ai_backend.model.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @CrossOrigin(origins = "*")
    @PostMapping("/matches")
    public Match createMatch(@RequestBody CreateMatchRequest request){

        Profile profile = profileRepo.findById(request.profileId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "unable to find a profile with ID: " + request.profileId));

        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                profile.id(),
                new ArrayList<>()
        );

        conversationRepo.save(conversation);
        Match match = new Match(UUID.randomUUID().toString(),
                profile,
                conversation.id());
        matchRepo.save(match);
        return match;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/matches")
    public List<Match> getAllMatches(){
        return matchRepo.findAll();
    }
}
