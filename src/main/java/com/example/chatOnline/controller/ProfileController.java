package com.example.chatOnline.controller;

import com.example.chatOnline.dto.UserDto;
import com.example.chatOnline.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;


@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomUserDetailsService userService;

    @GetMapping
    public String showUserSettingsProfile(Model model, Principal principal){
        UserDto curUser = userService.getUserProfile(principal.getName());
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

    @GetMapping("/users/{userId}")
    public String viewUserProfile(@PathVariable Long userId,
                                  Model model,
                                  Principal principal){
        UserDto userProfile = userService.getUserProfile(userId);
        boolean isOwnProfile = principal.getName().equals(userProfile.getUsername());

        model.addAttribute("user", userProfile);
        model.addAttribute("isOwnProfile", isOwnProfile);

        return "user-profile";
    }


}
