package com.example.chatOnline.service;

import java.util.Collection;
import java.util.Collections;

import com.example.chatOnline.dto.UserDto;
import com.example.chatOnline.exception.UserNotFoundException;
import com.example.chatOnline.mapper.UserMapper;
import org.springframework.resilience.annotation.RetryAnnotationBeanPostProcessor;
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

    private final UserMapper userMapper;

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

    public UserDto getUserProfile(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDto(user);

    }

}
