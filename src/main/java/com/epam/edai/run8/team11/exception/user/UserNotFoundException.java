package com.epam.edai.run8.team11.exception.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    public UserNotFoundException(String email){
        super("User not found with email: " + email);
    }

    public UserNotFoundException(String field, String value){
        super(String.format("User not found with %s: %s", field, value));
    }
}
