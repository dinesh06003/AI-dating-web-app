package com.dinesh.tinder_with_ai_backend.controller;


import com.dinesh.tinder_with_ai_backend.Repo.ConversationRepo;
import com.dinesh.tinder_with_ai_backend.Repo.ProfileRepo;
import com.dinesh.tinder_with_ai_backend.model.ChatMessage;
import com.dinesh.tinder_with_ai_backend.model.Conversation;
import com.dinesh.tinder_with_ai_backend.model.Profile;
import com.dinesh.tinder_with_ai_backend.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
//@RequestMapping("/api")
public class ConversationController {

    private final ConversationRepo conversationRepo;

    private final ProfileRepo profileRepo;

    private final ConversationService conversationService;

    public ConversationController(ConversationRepo conversationRepo, ProfileRepo profileRepo, ConversationService conversationService) {
        this.conversationRepo = conversationRepo;
        this.profileRepo = profileRepo;
        this.conversationService = conversationService;
    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("/conversations")
//    public Conversation createNewConversations(@RequestBody CreateConversationRequest request){
//        profileRepo.findById(request.profileId()).
//                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"unable to find profile with the Id" + request.profileId()));
//
//        Conversation conversation = new Conversation(
//                UUID.randomUUID().toString(),
//                request.profileId(),
//                new ArrayList<>());
//
//        conversationRepo.save(conversation);
//        return conversation;
//
//    }

    @CrossOrigin(origins = "*")
    @PostMapping("/conversations/{conversationId}")
    public Conversation addMessageToConversation(@PathVariable String conversationId, @RequestBody ChatMessage chatMessage){

        Conversation conversation = conversationRepo.findById(conversationId).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find conversation with the ID" + conversationId));
        String matchProfileId = conversation.profileId();
        Profile profile = profileRepo.findById(matchProfileId).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"unable to find profile with the Id" + matchProfileId));

        Profile user = profileRepo.findById(chatMessage.authorId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Unable to find a profile with Id: " + chatMessage.authorId()));
        ChatMessage messageWithTime = new ChatMessage(
                chatMessage.messageText(), chatMessage.authorId(), LocalDateTime.now()
        );
        conversation.messages().add(messageWithTime);
        conversationService.generateProfileResponse(conversation, profile, user);
        conversationRepo.save(conversation);
        return conversation;
    }

    public record CreateConversationRequest(String profileId){

    }


    @CrossOrigin(origins = "*")
    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversation(@PathVariable String conversationId){

        return conversationRepo.findById(conversationId)
                .orElseThrow(()-> new ResponseStatusException( HttpStatus.NOT_FOUND,"Conversation not found with id: " + conversationId));

    }


}
