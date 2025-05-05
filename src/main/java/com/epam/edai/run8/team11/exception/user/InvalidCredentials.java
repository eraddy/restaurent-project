package com.epam.edai.run8.team11.exception.user;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials(String email) {
        super("Invalid Credentials for emailID -> " + email);
    }
}
