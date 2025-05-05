package com.epam.edai.run8.team11.model.dish.dishstate;


import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DishStateConverter implements AttributeConverter<DishState> {

    @Override
    public AttributeValue transformFrom(DishState input) {
        return AttributeValue.builder().s(input != null ? input.getValue() : null).build();
    }

    @Override
    public DishState transformTo(AttributeValue input) {
        return input.s() != null ? DishState.fromValue(input.s()) : null;
    }

    @Override
    public EnhancedType<DishState> type() {
        return EnhancedType.of(DishState.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
