package com.example.chatOnline.service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.chatOnline.dto.UserDto;
import com.example.chatOnline.exception.ConversationNotFoundException;
import com.example.chatOnline.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.PostMapping;

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
        if (message.getContent().length() > 90) {
            conversation.setLastMessage(message.getContent().substring(0, 87)+"...");
        }
        else{
            conversation.setLastMessage(message.getContent());
        }
        
        conversationRepository.save(conversation);
        Message savedMessage = messageRepository.save(message);

        return messageMapper.toDto(savedMessage);
    }

    public List<ConversationDto> getAllUserConversation(String username){
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User with username - " + username + " doesn't exist"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllConversationByUser(currentUser);

        return chatParticipants.stream().map(participantConversation -> {
            Conversation conversation = participantConversation.getConversation();

            User targetUser = conversation.getParticipants().stream()
                    .filter((p) -> !p.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .map(ChatParticipant::getUser)
                    .orElse(null);


            String conversationName = calculateConversationName(conversation, targetUser);
            String conversationPicture = calculateConversationPicture(conversation, targetUser);

            if (conversation.getLastMessage() == null || conversation.getLastMessage().isEmpty()){
                conversation.setLastMessage("No messages yet");
            }
            conversation.setConversationName(conversationName);
            conversation.setConversationPicture(conversationPicture);

            if (targetUser == null){
                return conversationMapper.toDto(conversation, null);
            }
            return conversationMapper.toDto(conversation, targetUser.getUsername());
        }).collect(Collectors.toList());
    }

    public List<MessageResponseDto> getHistoryOfConversation(Long conversationId, String username, int pageNumber, int pageSize) {
        User curUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username - " + username + " can't found"));


        if (!chatParticipantRepository.existsByUserIdAndConversationId(curUser.getId(), conversationId)) {
            throw new AccessDeniedException("You are not a participant in this conversation");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Message> historyOfMessages = messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable)
                .orElseThrow(() -> new RuntimeException("Conversation doesn't exist"));

        List<MessageResponseDto> historyOfMessagesDtos = historyOfMessages.getContent().stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
        Collections.reverse(historyOfMessagesDtos);
        return historyOfMessagesDtos;
    }

    public Long getOrCreatePrivateConversation(String curUsername, String targetUsername){
        User curUser = userRepository.findByUsername(curUsername)
                .orElseThrow(() -> new UserNotFoundException("User - " + curUsername + " not found"));
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("User - " + targetUsername + " not found"));

        Optional<Long> targetConversation = chatParticipantRepository.getConversationIdOfDialogue(curUser.getId(), targetUser.getId());

        return targetConversation.orElseGet(() -> createPrivateConversation(curUser, targetUser));

    }

    @Transactional
    private Long createPrivateConversation(User curUser, User targetUser){

        Conversation newConversation = Conversation.builder()
                .conversationType(ConversationType.PRIVATE)
                .build();
        conversationRepository.save(newConversation);

        ChatParticipant curUserParticipant = ChatParticipant.builder()
                .conversation(newConversation)
                .user(curUser)
                .build();
        ChatParticipant targetUserParticipant = ChatParticipant.builder()
                .conversation(newConversation)
                .user(targetUser)
                .build();
        chatParticipantRepository.saveAll(List.of(curUserParticipant, targetUserParticipant));


        return newConversation.getId();
    }

    private String calculateConversationName(Conversation conversation, User targetUser){
        if (conversation.getConversationType() == ConversationType.GROUP){
            return conversation.getConversationName();
        }
        else{
            return targetUser.getNickname();
        }
    }


    private String calculateConversationPicture(Conversation conversation, User targetUser){
        // If it's a group then just return group name 
        if (conversation.getConversationType() == ConversationType.GROUP){
            if (conversation.getConversationPicture() == null || conversation.getConversationPicture().isEmpty()){
                return "https://ui-avatars.com/api/?name=" + conversation.getConversationName() + "&background=0D8ABC&color=fff";
            }
            else{
                return "/api/images/" + conversation.getConversationPicture();
            }
        }



        if (targetUser != null){
                if (targetUser.getProfilePicture() == null || targetUser.getProfilePicture().isEmpty()){
                    return "https://ui-avatars.com/api/?name=" + targetUser.getNickname() + "&background=0D8ABC&color=fff";
                }
                else {
                    return "/api/images/" + targetUser.getProfilePicture();
                }
        }
        return "https://ui-avatars.com/api/?name=??";
    }
}
