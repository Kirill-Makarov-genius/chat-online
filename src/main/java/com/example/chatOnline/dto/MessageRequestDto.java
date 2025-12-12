package com.example.chatOnline.dto;

import com.example.chatOnline.entity.Conversation;
import com.example.chatOnline.enums.MessageStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDto{

    @NotNull(message="Conversation ID is required")
    private Long conversationId;

    @NotBlank(message = "Message content cannot be empty")
    private String content;

}