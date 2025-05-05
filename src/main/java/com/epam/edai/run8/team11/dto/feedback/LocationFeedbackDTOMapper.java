package com.epam.edai.run8.team11.dto.feedback;

import com.epam.edai.run8.team11.model.feedback.Feedback;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LocationFeedbackDTOMapper implements Function<Feedback, LocationFeedbackDTO> {
    @Override
    public LocationFeedbackDTO apply(Feedback feedback) {
        return LocationFeedbackDTO.builder()
                .id(feedback.getFeedbackId())
                .type(feedback.getType())
                .date(feedback.getDate())
                .rating(feedback.getRating())
                .comment(feedback.getReview())
                .author("Default Author")
                .build();
    }
}
