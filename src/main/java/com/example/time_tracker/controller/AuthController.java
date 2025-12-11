package com.example.time_tracker.controller;

import org.springframework.stereotype.Controller;

import com.example.time_tracker.entity.User;
import com.example.time_tracker.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequiredArgsConstructor
public class AuthController {
    

    private final CustomUserDetailsService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/login?registered";
    }
    



    
    
    



}
