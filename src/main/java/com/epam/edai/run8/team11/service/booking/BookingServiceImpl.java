package com.epam.edai.run8.team11.service.booking;

import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByClient;
import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByWaiter;
import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.dto.sqs.EventPayloadDTO;
import com.epam.edai.run8.team11.dto.sqs.eventtype.EventType;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.exception.access.InvalidAccessException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.exception.waiter.WaiterNotFoundException;
import com.epam.edai.run8.team11.model.user.role.Role;
import com.epam.edai.run8.team11.model.Table;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.model.reservation.clienttype.ClientType;
import com.epam.edai.run8.team11.model.reservation.reservationstatus.ReservationStatus;
import com.epam.edai.run8.team11.service.location.LocationService;
import com.epam.edai.run8.team11.service.reservaiton.ReservationService;
import com.epam.edai.run8.team11.service.sqs.SqsService;
import com.epam.edai.run8.team11.service.table.TableService;
import com.epam.edai.run8.team11.service.waiter.WaiterService;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final WaiterService waiterService;
    private final LocationService locationService;
    private final ReservationService reservationService;
    private final AuthenticationUtil authenticationUtil;
    private final TableService tableService;
    private final SqsService sqsService;

    @Override
    public ReservationResponse bookingByClient(ReservationRequestByClient reservationRequestByClient) {
        log.debug("Processing booking request: {}", reservationRequestByClient);

        reservationRequestByClient.validate();
        UserDto userDto = authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);

        if(!userDto.getRole().equals(Role.CUSTOMER)) {
            throw new InvalidAccessException("You don't have access to perform this action");
        }

        String locationId = reservationRequestByClient.getLocationId();
        Integer tableNumber = Integer.valueOf(reservationRequestByClient.getTableNumber());
        LocalDate date = LocalDate.parse(reservationRequestByClient.getDate());
        String timeFrom = reservationRequestByClient.getTimeFrom();
        String timeTo = reservationRequestByClient.getTimeTo();
        Integer guestsNumber = reservationRequestByClient.getGuestsNumber();

        Table table = tableService.findByIdAndNumber(locationId,tableNumber);
        Waiter waiter = waiterService.getLeastBusyWaiterAtLocation(locationId, date, timeFrom);

        if (!tableService.isTableAvailable(table,date, timeFrom)) {
            log.error("Table {} is unavailable for locationId {} on {} at {}", tableNumber, locationId, date, timeFrom);
            throw new SlotAlreadyBookedException("Table is already booked");
        }

        if (waiter == null) {
            log.error("No waiter found at locationId: {}", locationId);
            throw new WaiterNotFoundException("No waiter available");
        }

        log.info("Assigned waiter: {} (ID: {}) for the reservation", waiter.getFirstName(), waiter.getUserId());

        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID().toString())
                .date(date.toString())
                .bookedByWaiter(false)
                .guestsNumber(guestsNumber.toString())
                .tableNumber(tableNumber)
                .locationId(locationId)
                .locationAddress(locationService.findLocationById(locationId).getAddress())
                .waiterId(waiter.getUserId())
                .customerName(userDto.getFullName())
                .customerId(userDto.getUserId())
                .clientType(ClientType.CUSTOMER)
                .waiterName(waiter.getFirstName())
                .timeSlot(timeFrom+" - "+timeTo)
                .status(ReservationStatus.CONFIRMED)
                .build();

        log.debug("Reservation created: {}", reservation);

        if (reservationService.makeReservation(reservation)) {
            log.info("Reservation successfully created: {}", reservation.getId());

            waiterService.bookWaiterSlots(waiter, date, timeFrom);
            tableService.bookTableSlots(table,date,timeFrom);
            waiter.setCount(waiter.getCount()+1);

            waiterService.updateWaiter(waiter);
            tableService.updateTable(table);

            log.info("Updated table slots for table {} on date {}: {}", tableNumber, date, table.getSlots().get(date.toString()));
        } else {
            log.error("Failed to create reservation: {}", reservation.getId());
        }

        EventPayloadDTO dto = EventPayloadDTO.builder()
                .eventType(EventType.ORDER)
                .reservationId(reservation.getId())
                .build();
        sqsService.sendMessage(dto);

        return ReservationResponse.builder()
                .id(reservation.getId())
                .status(ReservationStatus.CONFIRMED)
                .waiterName(waiter.getFirstName())
                .waiterId(waiter.getUserId())
                .customerId(userDto.getUserId())
                .customerName(userDto.getFullName())
                .clientType(ClientType.CUSTOMER)
                .date(date.toString())
                .locationId(locationId)
                .locationAddress(reservation.getLocationAddress())
                .timeSlot(timeFrom + "-" + timeTo)
                .tableNumber(tableNumber.toString())
                .guestsNumber(guestsNumber)
                .build();
    }

    @Override
    public List<String> getAvailableSlotsForWaiter(LocalDate date) {
        return waiterService.findAvailableSlots(date);
    }


    @Override
    public ReservationResponse bookingByWaiter(ReservationRequestByWaiter reservationRequestByWaiter) {

        reservationRequestByWaiter.validate();

        String locationId = reservationRequestByWaiter.getLocationId();
        Integer tableNumber = Integer.valueOf(reservationRequestByWaiter.getTableNumber());
        LocalDate date = LocalDate.parse(reservationRequestByWaiter.getDate());
        String timeFrom = reservationRequestByWaiter.getTimeFrom();
        String timeTo = reservationRequestByWaiter.getTimeTo();
        Integer guestsNumber = reservationRequestByWaiter.getGuestsNumber();
        String customerName = reservationRequestByWaiter.getCustomerName();
        String customerId = reservationRequestByWaiter.getCustomerId();
        ClientType clientType = ClientType.fromValue(reservationRequestByWaiter.getClientType());

        UserDto userDto = authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);

        if(!userDto.getRole().equals(Role.WAITER)) {
            throw new InvalidAccessException("You have no access to perform this action");
        }
        String waiterId = userDto.getUserId();

        Table table = tableService.findByIdAndNumber(locationId, tableNumber);
        Waiter waiter = waiterService.findWaiterById(waiterId);

        if (!tableService.isTableAvailable(table, date, timeFrom)) {
            log.error("Table {} is unavailable for locationId {} on {} at {}", tableNumber, locationId, date, timeFrom);
            throw new SlotAlreadyBookedException("Table is already booked");
        }

        if (waiter == null) {
            log.error("Waiter not found with ID: {}", waiter);
            throw new WaiterNotFoundException("No waiter found with the given ID");
        }

        // Check if the specified waiter is free at the given time slot
        if (!waiterService.isWaiterAvailable(waiter, date, timeFrom)) {
            log.error("Waiter {} (ID: {}) is unavailable on {} at {}", waiter.getFirstName(), waiter, date, timeFrom);
            throw new WaiterNotFoundException("Waiter is unavailable at the requested time");
        }

        log.info("Assigned waiter: {} (ID: {}) for the reservation", waiter.getFirstName(), waiter.getUserId());

        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID().toString())
                .date(date.toString())
                .bookedByWaiter(true)
                .guestsNumber(guestsNumber.toString())
                .tableNumber(tableNumber)
                .locationId(locationId)
                .locationAddress(locationService.findLocationById(locationId).getAddress())
                .waiterId(waiter.getUserId())
                .waiterName(waiter.getFirstName())
                .customerId(customerId)
                .customerName(customerName)
                .clientType(clientType)
                .timeSlot(timeFrom+" - "+timeTo)
                .status(ReservationStatus.IN_PROGRESS)
                .build();

        log.debug("Reservation created: {}", reservation);

        if (reservationService.makeReservation(reservation)) {
            log.info("Reservation successfully created by waiter: {}", reservation.getId());

            waiterService.bookWaiterSlots(waiter, date, timeFrom);
            tableService.bookTableSlots(table, date, timeFrom);
            waiter.setCount(waiter.getCount()+1);

            waiterService.updateWaiter(waiter);
            tableService.updateTable(table);

            log.info("Updated table slots for table {} on date {}: {}", tableNumber, date, table.getSlots().get(date.toString()));
        } else {
            log.error("Failed to create reservation by waiter: {}", reservation.getId());
        }

        EventPayloadDTO dto = EventPayloadDTO.builder()
                .eventType(EventType.ORDER)
                .reservationId(reservation.getId())
                .build();
        sqsService.sendMessage(dto);

        return ReservationResponse.builder()
                .id(reservation.getId())
                .status(ReservationStatus.IN_PROGRESS)
                .waiterName(waiter.getFirstName())
                .waiterId(waiter.getUserId())
                .customerId(customerId)
                .customerName(customerName)
                .clientType(clientType)
                .date(date.toString())
                .locationId(locationId)
                .locationAddress(reservation.getLocationAddress())
                .timeSlot(timeFrom + "-" + timeTo)
                .tableNumber(tableNumber.toString())
                .guestsNumber(guestsNumber)
                .build();
    }
}