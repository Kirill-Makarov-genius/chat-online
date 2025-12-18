package com.example.chatOnline.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.chatOnline.entity.ChatParticipant;
import com.example.chatOnline.entity.User;



@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long>{

    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);

    @Query("""
        SELECT p1.conversation.id
        FROM ChatParticipant p1
        JOIN ChatParticipant p2 ON p1.conversation.id = p2.conversation.id
        WHERE p1.user.id = :userId
            AND p2.user.id = :targetUserId
            AND p1.conversation.conversationType = 'PRIVATE'
    """)
    Optional<Long> getConversationIdOfDialogue(Long userId, Long targetUserId);


    @Query("""
        SELECT DISTINCT p
        FROM ChatParticipant p
        JOIN FETCH p.conversation c
        LEFT JOIN FETCH c.participants
        WHERE p.user = :user
""")
    List<ChatParticipant> findAllConversationByUser(@Param("user") User user);
}
