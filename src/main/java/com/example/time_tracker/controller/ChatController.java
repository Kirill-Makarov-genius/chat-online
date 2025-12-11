package com.example.time_tracker.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.time_tracker.dto.ChatMessage;
import org.springframework.web.bind.annotation.GetMapping;




@Controller
public class ChatController {
    

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {return message;};


    @GetMapping("/chats")
    public String allChatsView() {
        return "chat";
    }
    
    

}
