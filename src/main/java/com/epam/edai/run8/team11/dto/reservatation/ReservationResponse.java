package com.epam.edai.run8.team11.dto.reservatation;

import com.epam.edai.run8.team11.model.reservation.clienttype.ClientType;
import com.epam.edai.run8.team11.model.reservation.reservationstatus.ReservationStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {
    private String id;   // Unique ID of the reservation
    private String locationId;      // The location where the reservation is made
    private String tableNumber;     // The table number for the reservation
    private String date;            // The date of the reservation
    private String timeSlot;        // Time range for the reservation (e.g., "18:00 - 19:30")
    private int guestsNumber;       // Number of guests for the reservation
    private ReservationStatus status;          // Confirmation status of the reservation ("Confirmed" / "Pending")
    private String waiterId;        // Assigned waiter ID
    private String waiterName;      // Assigned waiter address
    private String locationAddress; // Address of the location
    private String customerId;
    private String customerName;
    private ClientType clientType;
}
