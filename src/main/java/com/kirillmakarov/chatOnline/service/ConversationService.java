package com.kirillmakarov.chatOnline.service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kirillmakarov.chatOnline.dto.*;
import com.kirillmakarov.chatOnline.exception.ConversationNotFoundException;
import com.kirillmakarov.chatOnline.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kirillmakarov.chatOnline.entity.ChatParticipant;
import com.kirillmakarov.chatOnline.entity.Conversation;
import com.kirillmakarov.chatOnline.entity.Message;
import com.kirillmakarov.chatOnline.entity.User;
import com.kirillmakarov.chatOnline.enums.ConversationType;
import com.kirillmakarov.chatOnline.enums.MessageStatus;
import com.kirillmakarov.chatOnline.mapper.ConversationMapper;
import com.kirillmakarov.chatOnline.mapper.MessageMapper;
import com.kirillmakarov.chatOnline.repository.ChatParticipantRepository;
import com.kirillmakarov.chatOnline.repository.ConversationRepository;
import com.kirillmakarov.chatOnline.repository.MessageRepository;
import com.kirillmakarov.chatOnline.repository.UserRepository;

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
    private final KafkaTemplate<String, NotificationEventDto> kafkaTemplate;

    private static final String NOTIFICATION_TOPIC = "chat-notifications";


    @Transactional
    public MessageResponseDto saveMessage(Long conversationId, MessageRequestDto request, String username){
        LocalDateTime now = LocalDateTime.now();
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
        message.setSentAt(now);

        String content = request.getContent();
        String preview = (content.length() > 50) ? content.substring(0, 47) + "..." : content;

        conversation.setLastMessage(preview);
        conversation.setLastMessageAt(now);
        
        conversationRepository.save(conversation);
        Message savedMessage = messageRepository.save(message);

        sendNotifications(conversationId, savedMessage, sender);

        return messageMapper.toDto(savedMessage);
    }

    //Send a notification about message
    public void sendNotifications(Long conversationId, Message message, User sender){

        List<User> participants = chatParticipantRepository.findUsersByConversationId(conversationId);

        for(User participant: participants){
            if (!participant.getUsername().equals(sender.getUsername())){
                NotificationEventDto notificationEvent = NotificationEventDto.builder()
                        .recipientUsername(participant.getUsername())
                        .senderUsername(sender.getUsername())
                        .content(message.getContent())
                        .conversationId(conversationId)
                        .build();
                kafkaTemplate.send(NOTIFICATION_TOPIC, participant.getUsername(), notificationEvent);
            }
        }
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
