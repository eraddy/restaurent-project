package com.epam.edai.run8.team11.service.reservaiton;

import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.dto.reservatation.UpdateReservationRequest;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.repository.reservation.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ReservationService {
    Reservation findById(String reservationId);
    boolean makeReservation(Reservation reservation);
    List<Reservation> findAll();
    String cancelReservation(String reservationId);
    void updateReservation(Reservation reservation);
    ReservationResponse updateReservation(String reservationId, UpdateReservationRequest updateReservationRequest);
}
