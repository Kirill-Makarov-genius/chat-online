package com.kirillmakarov.chatOnline.seeder;



import com.kirillmakarov.chatOnline.dto.UserDto;
import com.kirillmakarov.chatOnline.entity.*;
import com.kirillmakarov.chatOnline.enums.ConversationType;
import com.kirillmakarov.chatOnline.enums.MessageStatus;
import com.kirillmakarov.chatOnline.exception.UserNotFoundException;
import com.kirillmakarov.chatOnline.mapper.UserMapper;
import com.kirillmakarov.chatOnline.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder; // Ensure you have a SecurityConfig

    private final UserMapper userMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // 1. Check if data already exists to prevent duplicates on restart
        if (userRepository.count() > 0) {
            System.out.println("Database already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding database...");

        // --- STEP 1: CREATE USERS ---
        UserDto john = createUser("john_doe", "john@kirillmakarov.com");
        UserDto jane = createUser("jane_doe", "jane@kirillmakarov.com");
        UserDto mike = createUser("mike_smith", "mike@kirillmakarov.com");



        // --- STEP 2: CREATE PRIVATE CHAT (John & Jane) ---
        Conversation privateChat = new Conversation();
        privateChat.setConversationType(ConversationType.PRIVATE);
        // privateChat.setConversationName(null); // Private chats usually have no name
        conversationRepository.save(privateChat);

        addParticipant(john, privateChat, false);
        addParticipant(jane, privateChat, false);

        createMessage(privateChat, john, "Hey Jane! How are you?");
        createMessage(privateChat, jane, "Hi John! I'm good, thanks.");
        createMessage(privateChat, john, "Want to work on that Spring project?");

        // --- STEP 3: CREATE GROUP CHAT (John, Jane, Mike) ---
        Conversation groupChat = new Conversation();
        groupChat.setConversationType(ConversationType.GROUP);
        groupChat.setConversationName("Dev Team Alpha");
        conversationRepository.save(groupChat);

        addParticipant(john, groupChat, true); // John is Admin
        addParticipant(jane, groupChat, false);
        addParticipant(mike, groupChat, false);

        createMessage(groupChat, john, "Welcome to the team channel!");
        createMessage(groupChat, mike, "Glad to be here!");
        createMessage(groupChat, jane, "Let's build something cool.");

        System.out.println("Database seeding completed successfully!");
    }

    // --- HELPER METHODS ---

    private UserDto createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setNickname(email);
        user.setPassword(passwordEncoder.encode("password123")); // Default password
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private void addParticipant(UserDto user, Conversation conversation, boolean isAdmin) {
        User userParticipant = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username - " + user.getUsername() + " doesn't exist"));
        ChatParticipant participant = ChatParticipant.builder()
                .user(userParticipant)
                .conversation(conversation)
                .isAdmin(isAdmin) // Matches your entity field name (typo in entity: isAmin vs isAdmin)
                .build();
        participantRepository.save(participant);
    }

    private void createMessage(Conversation conversation, UserDto sender, String content) {
        User userSender = userRepository.findByUsername(sender.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username - " + sender.getUsername() + " not found!"));
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(userSender);
        message.setContent(content);
        message.setStatus(MessageStatus.SENT); // Enum: SENT, DELIVERED, READ
        
        // Slight delay to ensure messages have different timestamps
        message.setSentAt(LocalDateTime.now()); 
        conversation.setLastMessage(message.getContent());
        conversationRepository.save(conversation);
        messageRepository.save(message);
        
        try { Thread.sleep(10); } catch (InterruptedException e) {} // Fake delay
    }
}