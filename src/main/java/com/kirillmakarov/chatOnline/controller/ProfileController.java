package com.kirillmakarov.chatOnline.controller;

import com.kirillmakarov.chatOnline.dto.UserDto;
import com.kirillmakarov.chatOnline.repository.UserRepository;
import com.kirillmakarov.chatOnline.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final UserRepository userRepository;

    @GetMapping
    public String showUserSettingsProfile(Model model, Principal principal){
        UserDto curUser = userService.getUserProfile(principal.getName());
        model.addAttribute("user", curUser);
        return "user-settings";
    }

    @PostMapping("/update")
    public String updateUserProfile(@Valid @ModelAttribute("user") UserDto user,
                                    BindingResult bindingResult,
                                    @RequestParam(value="profileImage", required = false) MultipartFile file,
                                    Principal principal){
        if (bindingResult.hasErrors()){
            return "user-settings";
        }
        String curUsername = principal.getName();
        userService.saveUserProfileSettings(user, file, curUsername);
        return "redirect:/profile";
    }

    @GetMapping("/users/{username}")
    public String viewUserProfile(@PathVariable String username,
                                  Model model,
                                  Principal principal){
        UserDto userProfile = userService.getUserProfile(username);
        boolean isOwnProfile = principal.getName().equals(userProfile.getUsername());

        model.addAttribute("user", userProfile);
        model.addAttribute("isOwnProfile", isOwnProfile);

        return "user-profile";
    }


}
