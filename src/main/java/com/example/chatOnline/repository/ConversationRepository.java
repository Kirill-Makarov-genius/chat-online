package com.example.chatOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatOnline.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
}
