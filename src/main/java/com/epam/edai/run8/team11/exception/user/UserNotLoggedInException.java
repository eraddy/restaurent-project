package com.epam.edai.run8.team11.exception.user;

public class UserNotLoggedInException extends RuntimeException {

    public UserNotLoggedInException(){
        super("You are not logged in");
    }

    public UserNotLoggedInException(String message) {
        super(message);
    }
}
