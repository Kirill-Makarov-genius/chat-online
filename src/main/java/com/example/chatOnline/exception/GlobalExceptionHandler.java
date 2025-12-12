package com.example.chatOnline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


// @ControllerAdvice
// public class GlobalExceptionHandler {
    

//     @ExceptionHandler(UserAlreadyExistsException.class)
//     @ResponseStatus(HttpStatus.CONFLICT)
//     public String handleUserExists(UserAlreadyExistsException ex, Model model){
//         model.addAttribute("errors", ex.getMessage());
//         model.addAttribute("registrationFormDto", model.getAttribute("registationFormDto"));
//         return "login";
//     }

// }
