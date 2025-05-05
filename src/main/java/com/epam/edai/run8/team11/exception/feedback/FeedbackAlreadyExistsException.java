package com.epam.edai.run8.team11.exception.feedback;

public class FeedbackAlreadyExistsException extends FeedbackException {
    public FeedbackAlreadyExistsException(String reservationId, String type) {
        super("Feedback of type " + type + " already exists for reservation: " + reservationId);
    }
}