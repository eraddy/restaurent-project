package com.epam.edai.run8.team11.model.reservation.reservationstatus;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ReservationStatus {
    CONFIRMED ("Confirmed"),
    IN_PROGRESS ("in-progress"),
    CANCELLED("Cancelled"),
    FINISHED ("Finished");

    private final String value;

    ReservationStatus(String value){
        this.value = value;
    }

    public static ReservationStatus fromValue(String value) {
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value; // Return the value field for string representation
    }
}
