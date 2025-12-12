package com.example.chatOnline.seeder;



import com.example.chatOnline.entity.*;
import com.example.chatOnline.enums.ConversationType;
import com.example.chatOnline.enums.MessageStatus;
import com.example.chatOnline.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder; // Ensure you have a SecurityConfig

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
        User john = createUser("john_doe", "john@example.com");
        User jane = createUser("jane_doe", "jane@example.com");
        User mike = createUser("mike_smith", "mike@example.com");

        userRepository.saveAll(Arrays.asList(john, jane, mike));

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

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setNickname(email);
        user.setPassword(passwordEncoder.encode("password123")); // Default password
        return user;
    }

    private void addParticipant(User user, Conversation conversation, boolean isAdmin) {
        ChatParticipant participant = ChatParticipant.builder()
                .user(user)
                .conversation(conversation)
                .isAmin(isAdmin) // Matches your entity field name (typo in entity: isAmin vs isAdmin)
                .build();
        participantRepository.save(participant);
    }

    private void createMessage(Conversation conversation, User sender, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
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