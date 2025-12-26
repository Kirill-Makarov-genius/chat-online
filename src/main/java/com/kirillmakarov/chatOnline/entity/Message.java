package com.kirillmakarov.chatOnline.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.kirillmakarov.chatOnline.enums.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="messages", indexes = {
        // Fast lookup by conversation id
        @Index(name = "idx_msg_conversation", columnList = "conversation_id"),
        // Fast sorting by date
        @Index(name = "idx_msg_sent_at", columnList = "sentAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @CreationTimestamp
    private LocalDateTime sentAt;


}
