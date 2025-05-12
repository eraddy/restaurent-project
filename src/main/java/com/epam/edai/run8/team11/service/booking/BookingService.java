package com.epam.edai.run8.team11.service.booking;

import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByClient;
import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByWaiter;
import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    ReservationResponse bookingByClient(ReservationRequestByClient reservationRequestByClient);
    List<String> getAvailableSlotsForWaiter(LocalDate date);
    ReservationResponse bookingByWaiter(ReservationRequestByWaiter reservationRequestByWaiter);
}