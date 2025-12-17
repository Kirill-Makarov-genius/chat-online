package com.example.chatOnline.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private ConversationRepository conversationRepository;
    @Mock private ChatParticipantRepository chatParticipantRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageMapper messageMapper;
    @Mock private ConversationMapper conversationMapper;

    @InjectMocks private ConversationService conversationService;

    private User user;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setNickname("Alice");

        conversation = new Conversation();
        conversation.setId(10L);
        conversation.setConversationType(ConversationType.PRIVATE);

        ChatParticipant p1 = new ChatParticipant();
        p1.setId(100L);
        p1.setConversation(conversation);
        p1.setUser(user);

        User bob = new User();
        bob.setId(2L);
        bob.setUsername("bob");
        bob.setNickname("Bob");

        ChatParticipant p2 = new ChatParticipant();
        p2.setId(101L);
        p2.setConversation(conversation);
        p2.setUser(bob);

        conversation.setParticipants(new HashSet<>(Arrays.asList(p1, p2)));
    }

    // saveMessage behaviors

    @Test
    void saveMessage_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        MessageRequestDto req = new MessageRequestDto();
        req.setContent("Hello");

        assertThatThrownBy(() -> conversationService.saveMessage(10L, req, "alice"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verifyNoInteractions(messageRepository, conversationRepository, chatParticipantRepository, messageMapper);
    }

    @Test
    void saveMessage_shouldThrowWhenConversationNotFound() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(conversationRepository.findById(10L)).thenReturn(Optional.empty());
        MessageRequestDto req = new MessageRequestDto();
        req.setContent("Hello");

        assertThatThrownBy(() -> conversationService.saveMessage(10L, req, "alice"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Conversation not found");

        verify(chatParticipantRepository, never()).existsByUserIdAndConversationId(anyLong(), anyLong());
    }

    @Test
    void saveMessage_shouldThrowWhenUserNotParticipant() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(chatParticipantRepository.existsByUserIdAndConversationId(1L, 10L)).thenReturn(false);
        MessageRequestDto req = new MessageRequestDto();
        req.setContent("Hello");

        assertThatThrownBy(() -> conversationService.saveMessage(10L, req, "alice"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not a participant");

        verify(messageRepository, never()).save(any());
    }

    @Test
    void saveMessage_shouldSetFullLastMessageWhenLengthIsAtMost90() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(chatParticipantRepository.existsByUserIdAndConversationId(1L, 10L)).thenReturn(true);

        MessageRequestDto req = new MessageRequestDto();
        req.setContent("Short content");

        Message saved = new Message();
        saved.setId(55L);
        saved.setContent(req.getContent());
        saved.setConversation(conversation);
        saved.setSender(user);
        saved.setSentAt(LocalDateTime.now());
        saved.setStatus(MessageStatus.SENT);

        when(messageRepository.save(any(Message.class))).thenReturn(saved);
        MessageResponseDto dto = new MessageResponseDto();
        when(messageMapper.toDto(saved)).thenReturn(dto);

        MessageResponseDto result = conversationService.saveMessage(10L, req, "alice");

        assertThat(result).isSameAs(dto);
        assertThat(conversation.getLastMessage()).isEqualTo("Short content");
        verify(conversationRepository).save(conversation);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void saveMessage_shouldTruncateLastMessageWhenLengthGreaterThan90() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(chatParticipantRepository.existsByUserIdAndConversationId(1L, 10L)).thenReturn(true);

        String longContent = "x".repeat(95);
        MessageRequestDto req = new MessageRequestDto();
        req.setContent(longContent);

        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));
        when(messageMapper.toDto(any(Message.class))).thenReturn(new MessageResponseDto());

        conversationService.saveMessage(10L, req, "alice");

        assertThat(conversation.getLastMessage()).hasSize(90);
        assertThat(conversation.getLastMessage()).endsWith("...");
        assertThat(conversation.getLastMessage().substring(0, 87)).isEqualTo(longContent.substring(0, 87));
        verify(conversationRepository).save(conversation);
    }

    // getAllUserConversation behaviors

    @Test
    void getAllUserConversation_shouldSetNoMessagesYetWhenLastMessageIsNullOrEmpty() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Conversation conv = new Conversation();
        conv.setId(20L);
        conv.setConversationType(ConversationType.GROUP);
        conv.setConversationName("Team");
        conv.setLastMessage(null);

        ChatParticipant cp = new ChatParticipant();
        cp.setConversation(conv);
        cp.setUser(user);

        when(chatParticipantRepository.findAllByUser(user)).thenReturn(new HashSet<>(Collections.singletonList(cp)));

        ConversationDto dto = new ConversationDto();
        when(conversationMapper.toDto(conv)).thenReturn(dto);

        List<ConversationDto> list = conversationService.getAllUserConversation("alice");

        assertThat(list).containsExactly(dto);
        assertThat(conv.getLastMessage()).isEqualTo("No messages yet");
    }

    @Test
    void getAllUserConversation_shouldUseOtherNicknameForPrivateConversationName() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Conversation conv = new Conversation();
        conv.setId(21L);
        conv.setConversationType(ConversationType.PRIVATE);

        // participants: current user and other
        ChatParticipant cp1 = new ChatParticipant();
        cp1.setConversation(conv);
        cp1.setUser(user);

        User other = new User();
        other.setId(5L);
        other.setNickname("Charlie");
        ChatParticipant cp2 = new ChatParticipant();
        cp2.setConversation(conv);
        cp2.setUser(other);

        conv.setParticipants(new HashSet<>(Arrays.asList(cp1, cp2)));

        ChatParticipant owning = new ChatParticipant();
        owning.setConversation(conv);
        owning.setUser(user);

        when(chatParticipantRepository.findAllByUser(user)).thenReturn(new HashSet<>(Collections.singletonList(owning)));

        ConversationDto dto = new ConversationDto();
        when(conversationMapper.toDto(conv)).thenReturn(dto);

        List<ConversationDto> list = conversationService.getAllUserConversation("alice");

        assertThat(list).containsExactly(dto);
        assertThat(conv.getConversationName()).isEqualTo("Charlie");
    }

    // getHistoryOfConversation behaviors

    @Test
    void getHistoryOfConversation_shouldThrowWhenNotParticipant() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(chatParticipantRepository.existsByUserIdAndConversationId(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> conversationService.getHistoryOfConversation(10L, "alice"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not a participant");
    }


}
