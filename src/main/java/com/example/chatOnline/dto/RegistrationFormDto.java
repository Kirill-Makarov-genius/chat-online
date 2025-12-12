package com.example.chatOnline.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationFormDto {

    @NotBlank(message = "Username cannot be empty")
    @Size(min=4, max=20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Nickname cannot be empty")
    @Size(min=4, max=20, message = "nickname must be between 4 and 20 characters")
    private String nickname;

}
