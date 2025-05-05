package com.epam.edai.run8.team11.exception.user;

public class UserNotLogedInException extends RuntimeException {
    public UserNotLogedInException(String message) {
        super(message);
    }
}
