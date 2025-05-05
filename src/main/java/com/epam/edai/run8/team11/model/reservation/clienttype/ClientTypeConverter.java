package com.epam.edai.run8.team11.model.reservation.clienttype;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ClientTypeConverter implements AttributeConverter<ClientType> {
    @Override
    public AttributeValue transformFrom(ClientType input) {
        return input != null ? AttributeValue.fromS(input.getValue()) : AttributeValue.fromNul(true);
    }

    @Override
    public ClientType transformTo(AttributeValue input) {
        return input != null ? ClientType.fromValue(input.s()) : null;
    }

    @Override
    public EnhancedType<ClientType> type() {
        return EnhancedType.of(ClientType.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
