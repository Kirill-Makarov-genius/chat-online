package com.example.chatOnline.exception;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

    @ExceptionHandler(FileTooLargeException.class)
    public String handlesMaxSizeException(FileTooLargeException ex,
                                          RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        return "redirect:/profile";

    }

 }
