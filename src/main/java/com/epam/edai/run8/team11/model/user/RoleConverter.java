package com.epam.edai.run8.team11.model.user;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class RoleConverter implements AttributeConverter<Role> {

    @Override
    public AttributeValue transformFrom(Role input) {
        // Converts Role enum to a String for DynamoDB storage
        return input != null ? AttributeValue.fromS(input.getValue()) : AttributeValue.fromNul(true);
    }

    @Override
    public Role transformTo(AttributeValue input) { return input != null ? Role.fromValue(input.s()) : null; }

    @Override
    public EnhancedType<Role> type() {
        // Specifies the type being used by this converter
        return EnhancedType.of(Role.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        // Specifies that this converter works with String attributes in DynamoDB
        return AttributeValueType.S;
    }
}