package com.example.chatOnline.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
