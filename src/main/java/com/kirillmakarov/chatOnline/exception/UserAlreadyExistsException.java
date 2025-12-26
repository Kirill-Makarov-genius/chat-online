package com.kirillmakarov.chatOnline.exception;



public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(String message){
        super(message);
    }
}
