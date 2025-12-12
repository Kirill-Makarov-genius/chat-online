package com.example.chatOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatOnline.entity.Message;
import java.util.List;
import com.example.chatOnline.entity.Conversation;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    public List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
    
}
