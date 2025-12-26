package com.kirillmakarov.chatOnline.mapper;

import org.springframework.stereotype.Component;

import com.kirillmakarov.chatOnline.dto.MessageResponseDto;
import com.kirillmakarov.chatOnline.entity.Message;

@Component
public class MessageMapper {
    

    public MessageResponseDto toDto(Message message){
        if (message == null) return null;

        return MessageResponseDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .conversationId(message.getConversation().getId())
                .status(message.getStatus().name())
                .sentAt(message.getSentAt())
                .build();
    }

}
