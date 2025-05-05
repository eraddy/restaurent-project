package com.epam.edai.run8.team11.exception.user;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String email){
        super("User already exists with email: " + email);
    }
}
