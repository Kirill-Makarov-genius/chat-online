package com.kirillmakarov.chatOnline.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    Long id;
    String username;
    @NotNull
    @Size(max = 50, message = "Nickname can't be more than 50 letters")
    String nickname;
    @Size(max = 100, message = "Status should be less than 100 letters")
    String status;
    @Size(max = 255, message = "Description shouldn't be more than 255")
    String description;;
    String profilePicture;

    public String getProfilePictureUrl() {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return "https://ui-avatars.com/api/?name=" + this.username + "&background=random&color=fff";
        }
        return "/api/images/" + profilePicture; // Uses your ImageController
    }
}