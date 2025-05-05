package com.epam.edai.run8.team11.model.reservation.clienttype;

import lombok.Getter;

@Getter
public enum ClientType {
    CUSTOMER ("customer"),
    VISITOR ("visitor");

    private final String value;

    ClientType(String value){
        this.value = value;
    }

    public static ClientType fromValue(String value) {
        for (ClientType type : ClientType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
    @Override
    public String toString() {
        return value; // Return the value field for string representation
    }
}
