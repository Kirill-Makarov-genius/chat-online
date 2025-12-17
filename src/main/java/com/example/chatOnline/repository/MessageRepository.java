package com.example.chatOnline.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatOnline.entity.Message;

import java.util.Optional;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    public Optional<Page<Message>> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);
    
}
