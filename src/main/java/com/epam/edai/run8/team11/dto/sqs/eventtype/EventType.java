package com.epam.edai.run8.team11.dto.sqs.eventtype;

import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import lombok.Getter;

@Getter
public enum EventType{
    WORKING_HOURS("workingHours"),
    ORDER("order"),
    FEEDBACK("feedback");

    private final String value;
    EventType(String value){
        this.value = value;
    }

    public static EventType fromValue(String value) {
        for (EventType type : EventType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
