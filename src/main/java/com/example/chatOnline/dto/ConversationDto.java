package com.example.chatOnline.dto;

import com.example.chatOnline.enums.ConversationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {
    private Long id;
    private String conversationName;
    private String lastMessage;
    private ConversationType conversationType;
    private boolean isActive;

}
