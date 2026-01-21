package com.kirillmakarov.chatOnline.service;


import com.kirillmakarov.chatOnline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //Get google userInfo
        OAuth2User googleUser = super.loadUser(userRequest);
        String googleId = googleUser.getAttribute("sub");

        // Get current user from Security Context
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        if (currentAuth != null && currentAuth.isAuthenticated()){
            String dbUsername = currentAuth.getName();

            Map<String, Object> modifiedAttributes = new HashMap<>(googleUser.getAttributes());
            modifiedAttributes.put("db_username", dbUsername);

            userRepository.findByUsername(dbUsername).ifPresent(user -> {
                user.setGoogleId(googleId);
                userRepository.save(user);
            });

            return new DefaultOAuth2User(
                    currentAuth.getAuthorities(),
                    modifiedAttributes,
                    "db_username"
            );
        }

        return googleUser;
    }
}
