package com.epam.edai.run8.team11.exception.waiter;

public class WaiterNotFoundException extends RuntimeException{
    public WaiterNotFoundException(String waiterId){
        super(String.format("Waiter not found with id: %s", waiterId));
    }public WaiterNotFoundException(){
        super("Waiter not found at the location");
    }

    public WaiterNotFoundException(String waiterId, boolean locationAssigned){
        super(String.format("Waiter with id: %s is not assigned to any location", waiterId));
    }
}
