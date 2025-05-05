package com.epam.edai.run8.team11.model.feedback.feedbacktype;


import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class FeedbackTypeConverter implements AttributeConverter<FeedbackType> {

    @Override
    public AttributeValue transformFrom(FeedbackType input) {
        return input != null ? AttributeValue.fromS(input.getValue()) : AttributeValue.fromNul(true);
    }

    @Override
    public FeedbackType transformTo(AttributeValue input) {
        return input.s() != null ? FeedbackType.fromValue(input.s()) : null;
    }

    @Override
    public EnhancedType<FeedbackType> type() {
        return EnhancedType.of(FeedbackType.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
