package com.epam.edai.run8.team11.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackUpdateDTO {
    public static final String RATING = "rating";
    public static final String REVIEW = "review";

    private Double rating;
    private String review;
}
