package com.epam.edai.run8.team11.dto.feedback;

import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("rate")
    private Double rating;
    private String comment;
    @JsonProperty("userName")
    private String author;
    private LocalDate date;
    private String userAvatarUrl;
}