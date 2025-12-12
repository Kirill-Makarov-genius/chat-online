package com.example.chatOnline.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.chatOnline.dto.ConversationDto;
import com.example.chatOnline.dto.MessageResponseDto;
import com.example.chatOnline.repository.ConversationRepository;
import com.example.chatOnline.repository.MessageRepository;
import com.example.chatOnline.service.ConversationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    


    @GetMapping
    public String viewConversationsHome(Model model, Principal principal) {
        List<ConversationDto> conversations = conversationService.getAllUserConversation(principal.getName());

        model.addAttribute("conversations", conversations);
        model.addAttribute("activeConversationId", null);
        return "chat-layout";
    }
    
    @GetMapping("/{conversationId}")
    public String viewActiveConversation(@PathVariable Long conversationId,
                                        Model model, Principal principal
    ) {
        String username = principal.getName();
        List<ConversationDto> conversations = conversationService.getAllUserConversation(username);
        //Mark active conversation for displaying it
        conversations.forEach(conversation -> conversation.setActive(conversation.getId().equals(conversationId)));
        
        List<MessageResponseDto> historyOfConversation = conversationService.getHistoryOfConversation(conversationId, username);

        model.addAttribute("conversations", conversations);
        model.addAttribute("activeConversationId", conversationId);
        model.addAttribute("chatHistory", historyOfConversation);
        model.addAttribute("username", username);
        
        return "chat-layout";

    }
    
}
