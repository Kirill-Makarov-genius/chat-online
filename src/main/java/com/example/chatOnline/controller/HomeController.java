package com.example.chatOnline.controller;


import com.example.chatOnline.dto.SupportMessageDto;
import com.example.chatOnline.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EmailService emailService;

    @GetMapping
    public String homePage(Model model, Principal principal){
        if (principal != null){
            model.addAttribute("username", principal.getName());
        }
        model.addAttribute("supportMessageDto", new SupportMessageDto());
        return "index";
    }

    @PostMapping("/support")
    public String sendSupportMessage(@Valid @ModelAttribute SupportMessageDto supportMessageDto,
                                     BindingResult bindingResult,
                                     Model model,
                                     Principal principal){
        if (bindingResult.hasErrors()){
            if (principal != null) model.addAttribute("username", principal.getName());
            return "index";
        }

        String senderName = (principal != null) ? principal.getName() : "Guest";
        String subject = "New Support Ticket from " + senderName;
        String fullMessage = "Sender Email: " + supportMessageDto.getEmail() + "\n\n" +
                "Username: " + senderName + "\n\n" +
                "Message:\n" + supportMessageDto.getMessage();
        //Send message to admin email
        emailService.sendSimpleEmail("kiryusha.makarov.04@mail.ru", subject, fullMessage);

        //Send message to user to inform that message received
        emailService.sendSimpleEmail(supportMessageDto.getEmail(), "We received your message", "Hi! Thanks for contacting support. We will reply soon");

        return "redirect:/?supportSent=true";
    }

}
