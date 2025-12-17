package com.example.chatOnline.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupportMessageDto {

    @NotBlank(message = "email field shoud be field")
    @Email(message = "Enter a valid email address")
    private String email;
    @NotBlank(message = "Message cannot be emty")
    @Size(min=10, max=1000, message = "Message must be between 10 and 1000 characters")
    private String message;
}
