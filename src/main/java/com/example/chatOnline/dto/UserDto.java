package com.example.chatOnline.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    Long id;
    String username;
    String nickname;
    String profilePicture;

    public String getProfilePictureUrl() {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return "https://ui-avatars.com/api/?name=" + this.username + "&background=random&color=fff";
        }
        return "/api/images/" + profilePicture; // Uses your ImageController
    }
}