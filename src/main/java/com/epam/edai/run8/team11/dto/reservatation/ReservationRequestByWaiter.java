package com.epam.edai.run8.team11.dto.reservatation;

import com.epam.edai.run8.team11.model.reservation.clienttype.ClientType;
import lombok.Data;

@Data
public class ReservationRequestByWaiter extends ReservationRequestByClient{
    private String customerName;
    private String customerId;
    private String clientType;

    @Override
    public void validate() {
        super.validate(); // Reuse validation logic from the parent class (ReservationRequestByClient)

        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be null or empty.");
        }

        if (customerId == null || customerId.isBlank()) {
            if(clientType.equalsIgnoreCase("CUSTOMER"))
                throw new IllegalArgumentException("Customer ID must not be null or empty.");
        }

        if (clientType == null || clientType.isBlank()) {
            throw new IllegalArgumentException("Client Type must not be null or empty.");
        }
        // Optional: You can also validate specific customer types if needed
        if (!isValidCustomerType(clientType)) {
            throw new IllegalArgumentException("Invalid customer type provided.");
        }
    }

    private boolean isValidCustomerType(String customerType) {
        try {
            // Attempt to parse the clientType into an enum value using the fromValue method
            ClientType.fromValue(customerType);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Return false if the value does not match any enum value
        }
    }
}
