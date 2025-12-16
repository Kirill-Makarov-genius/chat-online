package com.example.chatOnline.controller;

import com.example.chatOnline.dto.UserDto;
import com.example.chatOnline.entity.User;
import com.example.chatOnline.repository.UserRepository;
import com.example.chatOnline.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;


@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomUserDetailsService userService;

    @GetMapping
    public String showUserProfile(Model model, Principal principal){
        UserDto curUser = userService.getUserProfile(principal.getName());
        System.out.println(curUser);
        model.addAttribute("userDto", curUser);
        return "user-settings";
    }

    @PostMapping("/update")
    public String updateUserProfile(@ModelAttribute UserDto userDto,
                                    @RequestParam(value="profileImage", required = false) MultipartFile file,
                                    Principal principal){
//        if (bindingResult.hasErrors()){
//            return "user-settings";
//        }
        String curUsername = principal.getName();
        userService.saveUserProfileSettings(userDto, file, curUsername);
        return "redirect:/profile";
    }


}
