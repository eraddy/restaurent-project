package com.epam.edai.run8.team11.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewFeedbackDTO {

    public static final String RESERVATION_ID = "reservationId";
    public static final String CUISINE_COMMENT = "cuisineComment";
    public static final String CUISINE_RATING = "cuisineRating";
    public static final String SERVICE_COMMENT = "serviceComment";
    public static final String SERVICE_RATING = "serviceRating";

    private String reservationId;
    private String cuisineComment;
    private Double cuisineRating;
    private String serviceComment;
    private Double serviceRating;
}
