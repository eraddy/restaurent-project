package com.epam.edai.run8.team11.exception;

import java.util.List;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message){
        super("Invalid input: " + message);
    }

    public InvalidInputException(String field, Object value){
        super(String.format("Invalid input: %s: %s", field, value));
    }

    public InvalidInputException(List<String> fields){
        super("Invalid input: " + fields);
    }
}
