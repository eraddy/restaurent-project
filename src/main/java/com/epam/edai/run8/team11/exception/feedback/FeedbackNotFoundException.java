package com.epam.edai.run8.team11.exception.feedback;

public class FeedbackNotFoundException extends RuntimeException{
    public FeedbackNotFoundException(String id){
        super("Feedback not found with id: " + id);
    }
}
