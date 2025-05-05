package com.epam.edai.run8.team11.model.user;

import lombok.Getter;
@Getter
public enum Role {
    CUSTOMER("customer"),
    WAITER("waiter"),
    ADMIN("admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    public String toString() {
        return value; // Return the value field for string representation
    }
}