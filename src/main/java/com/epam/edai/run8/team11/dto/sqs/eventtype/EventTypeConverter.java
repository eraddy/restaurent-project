package com.epam.edai.run8.team11.dto.sqs.eventtype;


import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class EventTypeConverter implements AttributeConverter<EventType> {

    @Override
    public AttributeValue transformFrom(EventType input) {
        return input != null ? AttributeValue.fromS(input.getValue()) : AttributeValue.fromNul(true);
    }

    @Override
    public EventType transformTo(AttributeValue input) {
        return input.s() != null ? EventType.fromValue(input.s()) : null;
    }

    @Override
    public EnhancedType<EventType> type() {
        return EnhancedType.of(EventType.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}

