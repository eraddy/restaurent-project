package com.epam.edai.run8.team11.dto.feedback;

import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationFeedbackDTO {
    private String id;
    private String locationId;
    private FeedbackType type;
    private Double rating;
    private String comment;
    private String author;
    private LocalDate date;
}