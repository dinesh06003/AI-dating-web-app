package com.dinesh.ai_dating_backend.controller;


import com.dinesh.ai_dating_backend.repo.ConversationRepo;
import com.dinesh.ai_dating_backend.repo.ProfileRepo;
import com.dinesh.ai_dating_backend.model.ChatMessage;
import com.dinesh.ai_dating_backend.model.Conversation;
import com.dinesh.ai_dating_backend.model.Profile;
import com.dinesh.ai_dating_backend.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationRepo conversationRepo;

    private final ProfileRepo profileRepo;

    private final ConversationService conversationService;

    public ConversationController(ConversationRepo conversationRepo, ProfileRepo profileRepo, ConversationService conversationService) {
        this.conversationRepo = conversationRepo;
        this.profileRepo = profileRepo;
        this.conversationService = conversationService;
    }

    @PostMapping("/{conversationId}")
    public Conversation addMessageToConversation(@PathVariable String conversationId, @RequestBody ChatMessage chatMessage){

        Conversation conversation = conversationRepo.findById(conversationId).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find conversation with the ID" + conversationId));
        String matchProfileId = conversation.profileId();
        Profile profile = profileRepo.findById(matchProfileId).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"unable to find profile with the Id" + matchProfileId));

        System.out.println("Auther ID:" + chatMessage.authorId());
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



    @GetMapping("/{conversationId}")
    public Conversation getConversation(@PathVariable String conversationId){

        return conversationRepo.findById(conversationId)
                .orElseThrow(()-> new ResponseStatusException( HttpStatus.NOT_FOUND,"Conversation not found with id: " + conversationId));

    }


}
