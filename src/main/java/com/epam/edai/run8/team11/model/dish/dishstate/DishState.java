package com.epam.edai.run8.team11.model.dish.dishstate;


import lombok.Getter;

@Getter
public enum DishState {
    AVAILABLE("Available"),
    OUT_OF_STOCK("Out of Stock"),
    UNKNOWN ("unknown");

    private final String value;

    DishState(String value) {
        this.value = value;
    }

    public static DishState fromValue(String value) {
        if(value == null || value.isEmpty())
            return UNKNOWN;
        for (DishState state : DishState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}