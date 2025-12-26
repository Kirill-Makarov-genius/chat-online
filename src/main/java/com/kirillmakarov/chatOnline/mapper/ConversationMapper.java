package com.kirillmakarov.chatOnline.mapper;

import org.springframework.stereotype.Component;

import com.kirillmakarov.chatOnline.dto.ConversationDto;
import com.kirillmakarov.chatOnline.entity.Conversation;

@Component
public class ConversationMapper {
    
    public ConversationDto toDto(Conversation conversation, String targetUsername){
        return ConversationDto.builder()
                .id(conversation.getId())
                .conversationName(conversation.getConversationName())
                .targetUsername(targetUsername)
                .lastMessage(conversation.getLastMessage())
                .conversationType(conversation.getConversationType())
                .conversationPicture(conversation.getConversationPicture())
                .build();
    }
}
