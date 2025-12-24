package com.example.chatOnline.service;


import com.example.chatOnline.dto.NotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics="chat-notifications", groupId = "chat-group")
    public void handleNotification(NotificationEventDto event){

        System.out.println("Received Kafka Event for: " + event.getRecipientUsername());

        messagingTemplate.convertAndSendToUser(
                event.getRecipientUsername(),
                "/queue/notifications",
                event
        );
    }

}
