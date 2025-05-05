package com.epam.edai.run8.team11.repository.reservation;

import com.epam.edai.run8.team11.model.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
   void putReservation(Reservation reservation);
   List<Reservation> reservationsForWaiter(String id);
   List<Reservation> reservationsForCustomer(String id);
   Optional<Reservation> getReservationById(String reservationId);
   void updateReservation(Reservation reservation);
}
