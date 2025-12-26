package com.kirillmakarov.chatOnline.dto;

import com.kirillmakarov.chatOnline.enums.ConversationType;

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
    private String targetUsername;
    private ConversationType conversationType;
    private String conversationPicture;
    private boolean isActive;


}
