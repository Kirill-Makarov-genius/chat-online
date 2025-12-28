package com.kirillmakarov.chatOnline.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class NotUniqueIdRoom extends RuntimeException {
    public NotUniqueIdRoom(String message) {
        super(message);
    }
}
