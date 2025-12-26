package com.kirillmakarov.chatOnline.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import com.kirillmakarov.chatOnline.dto.RegistrationFormDto;
import com.kirillmakarov.chatOnline.entity.User;
import com.kirillmakarov.chatOnline.exception.UserAlreadyExistsException;
import com.kirillmakarov.chatOnline.service.CustomUserDetailsService;

import org.springframework.web.bind.annotation.PostMapping;


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
