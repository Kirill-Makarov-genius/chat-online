package com.kirillmakarov.chatOnline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException(String message) {
        super(message);
    }
}
