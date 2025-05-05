package com.epam.edai.run8.team11.model.reservation.reservationstatus;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ReservationStatusConverter implements AttributeConverter<ReservationStatus> {
    @Override
    public AttributeValue transformFrom(ReservationStatus input) {
        return input != null ? AttributeValue.fromS(input.getValue()) : AttributeValue.fromNul(true);
    }

    @Override
    public ReservationStatus transformTo(AttributeValue input) {
        return input != null ? ReservationStatus.fromValue(input.s()) : null;
    }

    @Override
    public EnhancedType<ReservationStatus> type() {
        return EnhancedType.of(ReservationStatus.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
