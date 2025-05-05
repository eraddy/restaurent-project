package com.epam.edai.run8.team11.exception.waiter;

public class WaiterAlreadyExistsException extends RuntimeException{
    public WaiterAlreadyExistsException(String email){
        super("Waiter already exists with email: " + email);
    }
}
