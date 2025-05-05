package com.epam.edai.run8.team11.exception.location;

public class InvalidLocationIdException extends LocationException{
    public InvalidLocationIdException(String id){
        super("Invalid locationId: " + id);
    }
}
