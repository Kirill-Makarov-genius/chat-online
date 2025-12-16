package com.example.chatOnline.mapper;

import com.example.chatOnline.dto.UserDto;
import com.example.chatOnline.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .description(user.getDescription())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}