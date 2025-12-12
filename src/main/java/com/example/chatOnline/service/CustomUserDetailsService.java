package com.example.chatOnline.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chatOnline.dto.RegistrationFormDto;
import com.example.chatOnline.entity.User;
import com.example.chatOnline.exception.UserAlreadyExistsException;
import com.example.chatOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void registerUser(RegistrationFormDto registrationFormDto){
        if (userRepository.existsByUsername(registrationFormDto.getUsername())){
            throw new UserAlreadyExistsException("User with this username already exists");
        }
        User newUser = User
                .builder()
                .username(registrationFormDto.getUsername())
                .nickname(registrationFormDto.getNickname())
                .password(passwordEncoder.encode(registrationFormDto.getPassword()))
                .build();
        userRepository.save(newUser);
    }
}
