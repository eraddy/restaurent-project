package com.epam.edai.run8.team11.exception.reservation;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
        super("Reservation not found for id : "+message);
    }
}
