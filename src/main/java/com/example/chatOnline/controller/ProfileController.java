package com.example.chatOnline.controller;

import com.example.chatOnline.dto.UserDto;
import com.example.chatOnline.repository.UserRepository;
import com.example.chatOnline.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomUserDetailsService userService;

    @GetMapping
    public String showUserProfile(Model model, Principal principal){
        UserDto curUser = userService.getUserProfile(principal.getName());

        model.addAttribute("user", curUser);
        return "user-settings";
    }


}
