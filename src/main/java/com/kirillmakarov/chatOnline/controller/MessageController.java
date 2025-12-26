package com.kirillmakarov.chatOnline.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.kirillmakarov.chatOnline.dto.MessageRequestDto;
import com.kirillmakarov.chatOnline.dto.MessageResponseDto;
import com.kirillmakarov.chatOnline.repository.MessageRepository;
import com.kirillmakarov.chatOnline.service.ConversationService;

import lombok.RequiredArgsConstructor;




@Controller
@RequiredArgsConstructor
public class MessageController {


    private final ConversationService conversationService;

    @MessageMapping("/conversation/{conversationId}/sendMessage")
    @SendTo("/topic/conversation/{conversationId}")
    public MessageResponseDto sendMessage(
        @DestinationVariable Long conversationId,
        @Payload MessageRequestDto request,
        Principal principal
    ){
        return conversationService.saveMessage(conversationId, request, principal.getName());
    }

}