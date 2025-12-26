package com.kirillmakarov.chatOnline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDto {

    private String recipientUsername;
    private String senderUsername;
    private String content;
    private Long conversationId;
}
