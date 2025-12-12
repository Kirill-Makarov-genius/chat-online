package com.example.chatOnline.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatOnline.entity.ChatParticipant;
import com.example.chatOnline.entity.User;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long>{
    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);

    Set<ChatParticipant> findAllByUser(User user);
}
