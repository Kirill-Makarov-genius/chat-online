package com.example.chatOnline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


 @ControllerAdvice
 public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public String handlerUserNotFoundException(UserNotFoundException ex, Model model){
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 404);
        return "error/404";
    }

    @ExceptionHandler(ConversationNotFoundException.class)
     public String handlerConversationNotFoundException(ConversationNotFoundException ex, Model model){
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 404);
        return "error/404";
    }
 }
