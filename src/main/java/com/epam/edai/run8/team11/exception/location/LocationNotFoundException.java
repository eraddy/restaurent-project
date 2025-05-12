package com.epam.edai.run8.team11.exception.location;

public class LocationNotFoundException extends LocationException{
    public LocationNotFoundException(String locationId){
        super("Location not found with id: " + locationId);
    }
}
