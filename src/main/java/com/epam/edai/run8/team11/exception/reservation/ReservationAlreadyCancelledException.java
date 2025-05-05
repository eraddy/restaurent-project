package com.epam.edai.run8.team11.exception.reservation;

public class ReservationAlreadyCancelledException extends RuntimeException{
    public ReservationAlreadyCancelledException(String id){
        super("Reservation already cancelled: " + id);
    }
}
