package com.kirillmakarov.chatOnline.repository;

import com.kirillmakarov.chatOnline.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kirillmakarov.chatOnline.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {


}
