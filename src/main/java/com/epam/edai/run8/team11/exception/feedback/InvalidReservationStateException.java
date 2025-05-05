package com.epam.edai.run8.team11.exception.feedback;

public class InvalidReservationStateException extends FeedbackException {
    public InvalidReservationStateException(String status) {
        super("Cannot leave feedback for a reservation with status: " + status);
    }
}