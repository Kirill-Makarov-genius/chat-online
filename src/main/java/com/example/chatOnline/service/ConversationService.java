package com.example.chatOnline.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatOnline.dto.ConversationDto;
import com.example.chatOnline.dto.MessageRequestDto;
import com.example.chatOnline.dto.MessageResponseDto;
import com.example.chatOnline.entity.ChatParticipant;
import com.example.chatOnline.entity.Conversation;
import com.example.chatOnline.entity.Message;
import com.example.chatOnline.entity.User;
import com.example.chatOnline.enums.ConversationType;
import com.example.chatOnline.enums.MessageStatus;
import com.example.chatOnline.mapper.ConversationMapper;
import com.example.chatOnline.mapper.MessageMapper;
import com.example.chatOnline.repository.ChatParticipantRepository;
import com.example.chatOnline.repository.ConversationRepository;
import com.example.chatOnline.repository.MessageRepository;
import com.example.chatOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;

    @Transactional
    public MessageResponseDto saveMessage(Long conversationId, MessageRequestDto request, String username){
        User sender = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

        boolean isParticipant = chatParticipantRepository.existsByUserIdAndConversationId(sender.getId(), conversationId);
        if (!isParticipant){
            throw new AccessDeniedException("You are not a participant of this conversation");
        }

        Message message = new Message();
        message.setContent(request.getContent());
        message.setConversation(conversation);
        message.setSender(sender);
        message.setStatus(MessageStatus.SENT);
        message.setSentAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        return messageMapper.toDto(savedMessage);
    }

    public List<ConversationDto> getAllUserConversation(String username){
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow();

        Set<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByUser(currentUser);

        return chatParticipants.stream().map(participantConversation -> {
            Conversation conversation = participantConversation.getConversation();

            String displayName = calculateDisplayName(conversation, currentUser);
            if (conversation.getLastMessage() == null || conversation.getLastMessage().isEmpty()){
                conversation.setLastMessage("No messages yet");
            }
            conversation.setConversationName(displayName);

            return conversationMapper.toDto(conversation);
        }).collect(Collectors.toList());
    }

    public List<MessageResponseDto> getHistoryOfConversation(Long conversationId, String username){
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        
        if (!chatParticipantRepository.existsByUserIdAndConversationId(currentUser.getId(), conversationId)){
            throw new AccessDeniedException("You are not a participant in this conversation");
        }

        List<Message> historyOfMessages = messageRepository.findByConversationOrderBySentAtAsc(conversation);

        return historyOfMessages.stream().map(messageMapper::toDto).collect(Collectors.toList());
        
        

    }





    private String calculateDisplayName(Conversation conversation, User currentUser){
        // If it's a group then just return group name 
        if (conversation.getConversationType() == ConversationType.GROUP){
            return conversation.getConversationName();
        }

        // If is's private chat we should find nickname of user that not equal current
        return conversation.getParticipants().stream()
            .filter(participant -> !participant.getUser().getId().equals(currentUser.getId()))
            .findFirst()
            .map(participant -> participant.getUser().getNickname())
            .orElse("Unknown User");
    }
}
