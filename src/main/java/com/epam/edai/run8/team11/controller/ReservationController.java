package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.dto.reservatation.UpdateReservationRequest;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.service.reservaiton.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Map<String,String >> cancelReservation(@PathVariable String reservationId) {
        return ResponseEntity.ok(Map.of("message",reservationService.cancelReservation(reservationId)));
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable String reservationId, @RequestBody UpdateReservationRequest updateReservationRequest)
    {
        return ResponseEntity.ok(reservationService.updateReservation(reservationId,updateReservationRequest));
    }
}