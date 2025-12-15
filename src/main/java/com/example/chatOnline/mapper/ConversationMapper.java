package com.example.chatOnline.mapper;

import org.springframework.stereotype.Component;

import com.example.chatOnline.dto.ConversationDto;
import com.example.chatOnline.entity.Conversation;

@Component
public class ConversationMapper {
    
    public ConversationDto toDto(Conversation conversation){
        return ConversationDto.builder()
                .id(conversation.getId())
                .conversationName(conversation.getConversationName())
                .lastMessage(conversation.getLastMessage())
                .conversationType(conversation.getConversationType())
                .conversationPicture(conversation.getConversationPicture())
                .build();
    }
}
