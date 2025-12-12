package com.example.chatOnline.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chatOnline.dto.RegistrationFormDto;
import com.example.chatOnline.entity.User;
import com.example.chatOnline.exception.UserAlreadyExistsException;
import com.example.chatOnline.service.CustomUserDetailsService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequiredArgsConstructor
public class AuthController {
    

    private final CustomUserDetailsService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("registrationFormDto", new RegistrationFormDto());
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegistrationFormDto registrationFormDto,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
            return "login";
        }
        try {
            userService.registerUser(registrationFormDto);
        } catch (UserAlreadyExistsException ex) {
            bindingResult.rejectValue("username", "error.username", ex.getMessage());
            return "login";
        }
        return "redirect:/login?registered";
    }
    



    
    
    



}
