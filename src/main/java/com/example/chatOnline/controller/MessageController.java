package com.example.chatOnline.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.chatOnline.dto.MessageRequestDto;
import com.example.chatOnline.dto.MessageResponseDto;
import com.example.chatOnline.repository.MessageRepository;
import com.example.chatOnline.service.ConversationService;

import lombok.RequiredArgsConstructor;




@Controller
@RequiredArgsConstructor
public class MessageController {


    private final ConversationService conversationService;

    @MessageMapping("/conversation/{conversationId}/sendMessage")
    @SendTo("/topic/conversation/{conversationId}")
    public MessageResponseDto sendMessage(
        @DestinationVariable Long converstaionId,
        @Payload MessageRequestDto request,
        Principal principal
    ){
        return conversationService.saveMessage(converstaionId, request, principal.getName());
    }

}