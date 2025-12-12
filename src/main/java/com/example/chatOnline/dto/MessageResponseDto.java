package com.example.chatOnline.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseDto {
    
    private Long id;
    private String content;

    private Long senderId;
    private String senderUsername;

    private Long conversationId;

    private String status;
    private LocalDateTime sentAt;
}
