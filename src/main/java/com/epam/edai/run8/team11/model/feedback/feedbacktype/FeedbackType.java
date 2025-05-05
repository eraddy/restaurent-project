package com.epam.edai.run8.team11.model.feedback.feedbacktype;

import lombok.Getter;

@Getter
public enum FeedbackType {
    CUISINE_EXPERIENCE("CUISINE_EXPERIENCE"),
    SERVICE_QUALITY("SERVICE_QUALITY");

    private final String value;

    FeedbackType(String value) {
        this.value = value;
    }

    public static FeedbackType fromValue(String value) {
        for (FeedbackType type : FeedbackType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value for FeedbackType: " + value);
    }
}
