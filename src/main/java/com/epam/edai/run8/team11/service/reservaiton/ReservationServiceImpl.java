package com.epam.edai.run8.team11.service.reservaiton;

import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.dto.reservatation.UpdateReservationRequest;
import com.epam.edai.run8.team11.dto.sqs.EventPayloadDTO;
import com.epam.edai.run8.team11.dto.sqs.eventtype.EventType;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.exception.access.InvalidAccessException;
import com.epam.edai.run8.team11.exception.reservation.NoUpdateRequiredException;
import com.epam.edai.run8.team11.exception.reservation.ReservationAlreadyCancelledException;
import com.epam.edai.run8.team11.exception.reservation.ReservationNotFoundException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.exception.waiter.WaiterNotFoundException;
import com.epam.edai.run8.team11.model.user.role.Role;
import com.epam.edai.run8.team11.model.Table;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.model.reservation.reservationstatus.ReservationStatus;
import com.epam.edai.run8.team11.repository.reservation.ReservationRepository;
import com.epam.edai.run8.team11.service.location.LocationService;
import com.epam.edai.run8.team11.service.sqs.SqsService;
import com.epam.edai.run8.team11.service.table.TableService;
import com.epam.edai.run8.team11.service.waiter.WaiterService;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;
    private final WaiterService waiterService;
    private final TableService tableService;
    private final LocationService locationService;
    private final AuthenticationUtil authenticationUtil;
    private final SqsService sqsService;

    @Override
    public Reservation findById(String reservationId) {
        return reservationRepository.getReservationById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    @Override
    public boolean makeReservation(Reservation reservation) {
        reservationRepository.putReservation(reservation);
        return true;
    }

    @Override
    public List<Reservation> findAll() {
        Optional<UserDto> authenticatedUser = authenticationUtil.getAuthenticatedUser();
        if(authenticatedUser.isEmpty())
        {
            throw new UserNotLoggedInException("You are not logged in");
        }
        UserDto userDto = authenticatedUser.get();
        Role role = userDto.getRole();
        String userId = userDto.getUserId();
        if(role.equals(Role.WAITER))
        {
            return reservationRepository.reservationsForWaiter(userId);
        }
        return reservationRepository.reservationsForCustomer(userId);
    }

    @Override
    public String cancelReservation(String reservationId) {

        log.info("Deletion started");
        authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);

        Reservation reservation = findById(reservationId);
        reservation.setStatus(ReservationStatus.CANCELLED);

        Waiter waiter = waiterService.findWaiterById(reservation.getWaiterId());
        Table table = tableService.findByIdAndNumber(reservation.getLocationId(),reservation.getTableNumber());

        waiter.getSlots().get(reservation.getDate()).add(reservation.getTimeSlot().split(" - ")[0].trim());
        waiter.setCount(waiter.getCount()-1);
        table.getSlots().get(reservation.getDate()).add(reservation.getTimeSlot().split(" - ")[0].trim());

        waiterService.updateWaiter(waiter);
        tableService.updateTable(table);
        reservationRepository.updateReservation(reservation);

        EventPayloadDTO dto = EventPayloadDTO.builder()
                .eventType(EventType.ORDER)
                .reservationId(reservation.getId())
                .build();
        sqsService.sendMessage(dto);

        return "Reservation cancelled successfully";
    }

    @Override
    public void updateReservation(Reservation reservation) {
        log.info("Updating reservation item: {}", reservation);
        authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);
        reservationRepository.updateReservation(reservation);
    }

    @Override
    public ReservationResponse updateReservation(String reservationId, UpdateReservationRequest updateReservationRequest) {
        log.info("Updating reservation with ID: {}", reservationId);

        // Fetch the existing reservation
        log.debug("Fetching existing reservation with ID: {}", reservationId);
        Reservation existingReservation = findById(reservationId);


        // Validate the update request
        updateReservationRequest.populateWithDefaultValues(existingReservation);
        updateReservationRequest.validate();

        UserDto userDto = authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);

        // Extract details from the request
        String locationId = updateReservationRequest.getLocationId();
        Integer tableNumber = Integer.valueOf(updateReservationRequest.getTableNumber());
        LocalDate date = LocalDate.parse(updateReservationRequest.getDate());
        String timeFrom = updateReservationRequest.getTimeFrom();
        String timeTo = updateReservationRequest.getTimeTo();
        Integer guestsNumber = updateReservationRequest.getGuestsNumber();

        String updatingUserId = userDto.getUserId();
        String role = userDto.getRole().toString();

        // Check if the reservation is already cancelled
        if (ReservationStatus.CANCELLED.equals(existingReservation.getStatus())) {
            log.error("Cannot update a cancelled reservation with ID: {}", reservationId);
            throw new ReservationAlreadyCancelledException("Cannot update a cancelled reservation.");
        }

        // Preserve old data if not provided
        locationId = locationId != null ? locationId : existingReservation.getLocationId();
        tableNumber = tableNumber != null ? tableNumber : existingReservation.getTableNumber();
        date = date != null ? date : LocalDate.parse(existingReservation.getDate());
        timeFrom = timeFrom != null ? timeFrom : existingReservation.getTimeSlot().split(" - ")[0].trim();
        timeTo = timeTo != null ? timeTo : existingReservation.getTimeSlot().split(" - ")[1].trim();
        guestsNumber = guestsNumber != null ? guestsNumber : Integer.valueOf(existingReservation.getGuestsNumber());

        if (locationId.equals(existingReservation.getLocationId()) &&
                tableNumber.equals(existingReservation.getTableNumber()) &&
                date.toString().equals(existingReservation.getDate()) &&
                timeFrom.equals(existingReservation.getTimeSlot().split(" - ")[0].trim()) &&
                timeTo.equals(existingReservation.getTimeSlot().split(" - ")[1].trim()) &&
                guestsNumber.equals(Integer.valueOf(existingReservation.getGuestsNumber())))
        {
            throw new NoUpdateRequiredException("Details provide match the existing reservation");
        }

        boolean isTableChanged = !tableNumber.equals(existingReservation.getTableNumber()) || !locationId.equals(existingReservation.getLocationId());
        boolean isTimeSlotChanged = !timeFrom.equals(existingReservation.getTimeSlot().split(" - ")[0].trim())
                || !timeTo.equals(existingReservation.getTimeSlot().split(" - ")[1].trim())
                || !date.toString().equals(existingReservation.getDate());

        if (!isTableChanged || !isTimeSlotChanged)
        {
            existingReservation.setGuestsNumber(guestsNumber.toString());
            reservationRepository.updateReservation(existingReservation);
            return ReservationResponse.builder()
                    .id(existingReservation.getId())
                    .status(ReservationStatus.CONFIRMED)
                    .waiterName(existingReservation.getWaiterName())
                    .waiterId(existingReservation.getWaiterId())
                    .date(existingReservation.getDate())
                    .locationId(existingReservation.getLocationId())
                    .locationAddress(
                            locationService.findLocationById(existingReservation.getLocationId())
                                    .getAddress())
                    .timeSlot(existingReservation.getTimeSlot())
                    .tableNumber(existingReservation.getTableNumber().toString())
                    .guestsNumber(Integer.valueOf(existingReservation.getGuestsNumber()))
                    .build();
        }

        // Check table availability
        log.debug("Checking table availability for locationId: {}, tableNumber: {}, date: {}, timeFrom: {}",
                locationId, tableNumber, date, timeFrom);
        Table table = tableService.findByIdAndNumber(locationId, tableNumber);
        if (!tableService.isTableAvailable(table, date, timeFrom)) {
            log.error("Table {} at location {} is unavailable on {} at {}", tableNumber, locationId, date, timeFrom);
            throw new SlotAlreadyBookedException("Table is already booked or unavailable.");
        }

        // Check guests capacity
        if (guestsNumber > table.getCapacity()) {
            log.error("Guests number {} exceeds table capacity {}", guestsNumber, table.getCapacity());
            throw new IllegalArgumentException("The requested guests number exceeds the table capacity.");
        }

        // Check and assign the waiter (if the updating user is a waiter)
        Waiter newWaiter = null;
        if ("waiter".equals(role)) {
            log.info("Request made by waiter ID: {}. Checking assignment eligibility...", updatingUserId);

            Waiter updatingWaiter = waiterService.findWaiterById(updatingUserId);
            if (!updatingWaiter.getLocationId().equals(locationId)) {
                log.error("Unauthorized update attempt: Waiter from locationId {} cannot update reservation at locationId {}.",
                        updatingWaiter.getLocationId(), locationId);
                throw new InvalidAccessException("Waiter can only update reservations from their assigned location.");
            }

            List<String> waiterSlotsForDate = new ArrayList<>(updatingWaiter.getSlots().getOrDefault(date.toString(), new ArrayList<>()));
            if (waiterSlotsForDate.contains(timeFrom)) {
                log.info("The updating waiter is assigning themselves to the reservation.");
                newWaiter = updatingWaiter;
            } else {
                log.error("Waiter ID: {} is not available for the requested time slot on {}", updatingUserId, date);
                throw new SlotAlreadyBookedException("You are not available for the requested time slot.");
            }
        }

        // Handle case for assigning the least busy waiter (if customer makes the request or if reassign is required)
        if (newWaiter == null) {
            log.debug("Finding an available waiter at locationId: {} for date: {} and timeFrom: {}", locationId, date, timeFrom);
            newWaiter = waiterService.getLeastBusyWaiterAtLocation(locationId, date, timeFrom);
            if (newWaiter == null) {
                log.error("No waiter available for locationId: {}", locationId);
                throw new WaiterNotFoundException("No available waiter found for the given time slot.");
            }
        }

        log.info("Assigned waiter: {} (ID: {}) successfully.", newWaiter.getFirstName(), newWaiter.getUserId());

        // Free up previous slots
        log.debug("Freeing up slots for the existing reservation.");
        Table previousTable = tableService.findByIdAndNumber(existingReservation.getLocationId(), existingReservation.getTableNumber());
        Waiter previousWaiter = waiterService.findWaiterById(existingReservation.getWaiterId());

        // Convert to mutable lists before modifying
        List<String> previousTableSlots = new ArrayList<>(previousTable.getSlots().get(existingReservation.getDate()));
        previousTableSlots.add(existingReservation.getTimeSlot().split(" - ")[0].trim());
        previousTable.getSlots().put(existingReservation.getDate(), previousTableSlots);
        tableService.updateTable(previousTable);

        if (previousWaiter != null) {
            List<String> previousWaiterSlots = new ArrayList<>(previousWaiter.getSlots().get(existingReservation.getDate()));
            previousWaiterSlots.add(existingReservation.getTimeSlot().split(" - ")[0].trim());
            previousWaiter.getSlots().put(existingReservation.getDate(), previousWaiterSlots);
            previousWaiter.setCount(previousWaiter.getCount() - 1);
            waiterService.updateWaiter(previousWaiter);
        }

        // Update slots for the new table and waiter
        List<String> newTableSlots = new ArrayList<>(table.getSlots().get(date.toString()));
        newTableSlots.remove(timeFrom);
        table.getSlots().put(date.toString(), newTableSlots);
        tableService.updateTable(table);

        List<String> newWaiterSlots = new ArrayList<>(newWaiter.getSlots().get(date.toString()));
        newWaiterSlots.remove(timeFrom);
        newWaiter.getSlots().put(date.toString(), newWaiterSlots);
        newWaiter.setCount(newWaiter.getCount() + 1);
        waiterService.updateWaiter(newWaiter);

        // Update the reservation
        existingReservation.setLocationId(locationId);
        existingReservation.setTableNumber(tableNumber);
        existingReservation.setDate(date.toString());
        existingReservation.setGuestsNumber(guestsNumber.toString());
        existingReservation.setTimeSlot(timeFrom + " - " + timeTo);
        existingReservation.setWaiterId(newWaiter.getUserId());
        existingReservation.setWaiterName(newWaiter.getFirstName());
        existingReservation.setStatus(ReservationStatus.CONFIRMED);

        log.debug("Updating reservation: {}", existingReservation);
        reservationRepository.updateReservation(existingReservation);

        // Return the response
        return ReservationResponse.builder()
                .id(existingReservation.getId())
                .status(ReservationStatus.CONFIRMED)
                .waiterName(newWaiter.getFirstName())
                .waiterId(newWaiter.getUserId())
                .customerId(existingReservation.getCustomerId())
                .customerName(existingReservation.getCustomerName())
                .clientType(existingReservation.getClientType())
                .date(existingReservation.getDate())
                .locationId(existingReservation.getLocationId())
                .locationAddress(locationService.findLocationById(existingReservation.getLocationId()).getAddress())
                .timeSlot(existingReservation.getTimeSlot())
                .tableNumber(existingReservation.getTableNumber().toString())
                .guestsNumber(Integer.valueOf(existingReservation.getGuestsNumber()))
                .build();
    }
}
