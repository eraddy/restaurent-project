package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByClient;
import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByWaiter;
import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.model.Table;
import com.epam.edai.run8.team11.service.booking.BookingService;
import com.epam.edai.run8.team11.service.table.TableService;
import com.epam.edai.run8.team11.utils.ResponseUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/bookings")
public class BookingController {

    @Autowired
    TableService tableService;
    @Autowired
    BookingService bookingService;
    @Autowired
    ResponseUtil responseUtil;

    @GetMapping("/tables")
    public ResponseEntity<List<Table>> getAllTables(@RequestParam String locationId,@RequestParam String date,@RequestParam int guestsNumber,@RequestParam String time)
    {
        List<Table> tables =  tableService.getAvailableTables(locationId,date,guestsNumber,time);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/tables/waiter")
    public ResponseEntity<Map<String, Object>> getAaviableSlotsForWaiter(@RequestParam LocalDate date)
    {
        List<String> availableSlots =  bookingService.getAvailableSlotsForWaiter(date);
        return responseUtil.buildOkResponse(Map.of("availableSlots", availableSlots));
    }

    @PostMapping("/client")
    public ResponseEntity<ReservationResponse> bookingByClient(@RequestBody ReservationRequestByClient reservationRequestByClient)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookingByClient(reservationRequestByClient));
    }

    @PostMapping("/waiter")
    public ResponseEntity<ReservationResponse> bookingByWaiter(@RequestBody ReservationRequestByWaiter reservationRequestByWaiter)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookingByWaiter(reservationRequestByWaiter));
    }

}
