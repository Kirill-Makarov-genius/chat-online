package com.kirillmakarov.chatOnline.service;



import com.kirillmakarov.chatOnline.dto.UserDto;
import com.kirillmakarov.chatOnline.exception.UserNotFoundException;
import com.kirillmakarov.chatOnline.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kirillmakarov.chatOnline.dto.RegistrationFormDto;
import com.kirillmakarov.chatOnline.entity.User;
import com.kirillmakarov.chatOnline.exception.UserAlreadyExistsException;
import com.kirillmakarov.chatOnline.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final FileStorageService fileStorageService;

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
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        return userMapper.toDto(user);

    }
    public UserDto getUserProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDto(user);

    }

    public void saveUserProfileSettings(UserDto userDto, MultipartFile filePicture, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + userDto.getUsername()));

        user.setNickname(userDto.getNickname());
        user.setStatus(userDto.getStatus());
        user.setDescription(userDto.getDescription());

        if (filePicture != null && !filePicture.isEmpty()){
            String fileName = fileStorageService.storeFile(filePicture);
            user.setProfilePicture(fileName);
        }
        userRepository.save(user);
    }

}
