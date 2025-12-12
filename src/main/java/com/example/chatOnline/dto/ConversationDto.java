package com.example.chatOnline.dto;

import com.example.chatOnline.enums.ConversationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationDto {
    private Long id;
    private String conversationName;
    private String lastMessage;
    private ConversationType conversationType;
    private boolean isActive;

}
