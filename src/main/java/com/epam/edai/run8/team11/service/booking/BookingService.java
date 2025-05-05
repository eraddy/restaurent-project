package com.epam.edai.run8.team11.service.booking;

import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByClient;
import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByWaiter;
import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.model.Table;

import java.util.List;

public interface BookingService {
    ReservationResponse bookingByClient(ReservationRequestByClient reservationRequestByClient);
    ReservationResponse bookingByWaiter(ReservationRequestByWaiter reservationRequestByWaiter);
}