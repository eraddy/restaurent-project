package com.epam.edai.run8.team11.service.reservaiton;

import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.dto.reservatation.UpdateReservationRequest;
import com.epam.edai.run8.team11.dto.sqs.EventPayloadDTO;
import com.epam.edai.run8.team11.dto.sqs.eventtype.EventType;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.exception.access.InvalidAccessException;
import com.epam.edai.run8.team11.exception.location.LocationNotFoundException;
import com.epam.edai.run8.team11.exception.reservation.NoUpdateRequiredException;
import com.epam.edai.run8.team11.exception.reservation.ReservationAlreadyCancelledException;
import com.epam.edai.run8.team11.exception.reservation.ReservationNotFoundException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.table.TableNotFoundException;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.exception.waiter.WaiterNotFoundException;
import com.epam.edai.run8.team11.model.user.role.Role;
import com.epam.edai.run8.team11.model.table.Table;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.model.reservation.reservationstatus.ReservationStatus;
import com.epam.edai.run8.team11.repository.reservation.ReservationRepository;
import com.epam.edai.run8.team11.service.location.LocationService;
import com.epam.edai.run8.team11.service.sqs.SqsService;
import com.epam.edai.run8.team11.service.table.TableService;
import com.epam.edai.run8.team11.service.waiter.WaiterService;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import com.epam.edai.run8.team11.utils.SlotUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
        log.info("Reservation found {}",reservation);
        if(reservation.getStatus().equals(ReservationStatus.CANCELLED))
            throw new ReservationAlreadyCancelledException(reservationId);
        reservation.setStatus(ReservationStatus.CANCELLED);

        Waiter waiter = waiterService.findWaiterById(reservation.getWaiterId());
        log.info("Waiter found {}",waiter);
        Table table = tableService.findByIdAndNumber(reservation.getLocationId(),reservation.getTableNumber());
        log.info("Table found {}",table);

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

        UserDto userDto = authenticationUtil.getAuthenticatedUser()
                .orElseThrow(() -> {
                    log.error("A user tried to update a reservation but is not logged in.");
                    return new UserNotLoggedInException();
                });

        log.info("Attempting to update reservation with ID: {}", reservationId);

        // Fetch the existing reservation
        log.debug("Fetching details for reservation ID: {}", reservationId);
        Reservation existingReservation = findById(reservationId);

        if (existingReservation == null) {
            log.error("Reservation with ID: {} not found.", reservationId);
            throw new ReservationNotFoundException("Reservation not found.");
        }

        log.info("Fetched reservation: {}", existingReservation);

        // Check if the reservation is already cancelled
        if (ReservationStatus.CANCELLED.equals(existingReservation.getStatus())) {
            log.error("Failed to update reservation ID: {} as it is already cancelled.", reservationId);
            throw new ReservationAlreadyCancelledException("Cannot update a cancelled reservation.");
        }

        // Populating and validating the update request
        log.debug("Populating default values and validating the update request.");
        updateReservationRequest.populateWithDefaultValues(existingReservation);
        updateReservationRequest.validate();

        // Extract details from the request
        String locationId = updateReservationRequest.getLocationId();
        Integer tableNumber = Integer.valueOf(updateReservationRequest.getTableNumber());
        LocalDate date = LocalDate.parse(updateReservationRequest.getDate());
        String timeFrom = updateReservationRequest.getTimeFrom();
        String timeTo = updateReservationRequest.getTimeTo();
        Integer guestsNumber = updateReservationRequest.getGuestsNumber();

        log.debug("Update request details - Location ID: {}, Table Number: {}, Date: {}, Time From: {}, Time To: {}, Guests Number: {}",
                locationId, tableNumber, date, timeFrom, timeTo, guestsNumber);

        String updatingUserId = userDto.getUserId();
        String role = userDto.getRole().toString();

        // Check if update is necessary
        if (locationId.equals(existingReservation.getLocationId()) &&
                tableNumber.equals(existingReservation.getTableNumber()) &&
                date.toString().equals(existingReservation.getDate()) &&
                timeFrom.equals(existingReservation.getTimeSlot().split(" - ")[0].trim()) &&
                timeTo.equals(existingReservation.getTimeSlot().split(" - ")[1].trim()) &&
                guestsNumber.equals(Integer.valueOf(existingReservation.getGuestsNumber()))) {
            log.warn("No update required: Provided details match the existing reservation.");
            throw new NoUpdateRequiredException("Details provided match the existing reservation.");
        }

        boolean isTableChanged = !tableNumber.equals(existingReservation.getTableNumber()) || !locationId.equals(existingReservation.getLocationId());
        boolean isTimeSlotChanged = !timeFrom.equals(existingReservation.getTimeSlot().split(" - ")[0].trim())
                || !timeTo.equals(existingReservation.getTimeSlot().split(" - ")[1].trim())
                || !date.toString().equals(existingReservation.getDate());

        // Handle guests number update without changing table or time slot
        if (!isTableChanged && !isTimeSlotChanged) {
            log.info("Guests number update detected. Performing partial update.");
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

        Table table1 = tableService.findByIdAndNumber(existingReservation.getLocationId(),existingReservation.getTableNumber());
        table1.getSlots().get(existingReservation.getDate()).add(existingReservation.getTimeSlot().split(" - ")[0].trim());
        tableService.updateTable(table1);

        // Validate table availability
        log.debug("Checking availability for table number: {} at location ID: {} for date: {} and time: {}",
                tableNumber, locationId, date, timeFrom);

        Table table = tableService.findByIdAndNumber(locationId, tableNumber);

        if (table == null) {
            log.error("Table number: {} not found at location ID: {}", tableNumber, locationId);
            throw new TableNotFoundException(tableNumber);
        }

        if (!tableService.isTableAvailable(table, date, timeFrom)) {
            log.error("Table number: {} at location ID: {} is unavailable on date: {} at time: {}", tableNumber, locationId, date, timeFrom);
            throw new SlotAlreadyBookedException("Table is already booked or unavailable.");
        }

        // Validate guests capacity
        if (guestsNumber > table.getCapacity()) {
            log.error("Guests number: {} exceeds capacity of table number: {} which can handle up to {} guests.", guestsNumber, tableNumber, table.getCapacity());
            throw new IllegalArgumentException("The requested guests number exceeds the table capacity.");
        }

        // Assign Waiter based on role (if updating user is a waiter)
        Waiter newWaiter = null;
        if ("waiter".equals(role)) {
            log.info("Update request made by waiter ID: {}. Checking eligibility...", updatingUserId);

            Waiter updatingWaiter = waiterService.findWaiterById(updatingUserId);
            if (!updatingWaiter.getLocationId().equals(locationId)) {
                log.error("Waiter ID: {} is not authorized to update reservations at location ID: {}.", updatingUserId, locationId);
                throw new InvalidAccessException("Waiter can only update reservations from their assigned location.");
            }

            List<String> waiterSlotsForDate = updatingWaiter.getSlots().getOrDefault(date.toString(), new ArrayList<>());
            if (waiterSlotsForDate.contains(timeFrom)) {
                log.info("Assigning waiter ID: {} to the reservation.", updatingUserId);
                newWaiter = updatingWaiter;
            } else {
                log.error("Waiter ID: {} is not available for the requested time slot on date: {}.", updatingUserId, date);
                throw new SlotAlreadyBookedException("You are not available for the requested time slot.");
            }
        }

        if (newWaiter == null) {
            log.debug("Finding least busy waiter for location ID: {} on date: {} and time: {}", locationId, date, timeFrom);
            newWaiter = waiterService.getLeastBusyWaiterAtLocation(locationId, date, timeFrom);
            if (newWaiter == null) {
                log.error("No eligible waiter found for location ID: {} on date: {} at time: {}.", locationId, date, timeFrom);
                throw new WaiterNotFoundException("No available waiter found for the given time slot.");
            }
        }

        log.info("Successfully assigned waiter: {} (ID: {}) to the reservation.", newWaiter.getFirstName(), newWaiter.getUserId());

        // Freeing previous resources and updating with new resources
        log.debug("Freeing up slots for previous table and waiter.");
        Waiter previousWaiter = waiterService.findWaiterById(existingReservation.getWaiterId());

        if (previousWaiter != null) {
            List<String> previousWaiterSlots = new ArrayList<>(previousWaiter.getSlots().get(existingReservation.getDate()));
            previousWaiterSlots.add(existingReservation.getTimeSlot().split(" - ")[0].trim());
            previousWaiter.getSlots().put(existingReservation.getDate(), previousWaiterSlots);
            previousWaiter.setCount(previousWaiter.getCount() - 1);
            waiterService.updateWaiter(previousWaiter);
        }

        log.debug("Updating slots for new table and waiter.");
        List<String> newTableSlots = new ArrayList<>(table.getSlots().getOrDefault(date.toString(), SlotUtil.getDefaultSlots()));
        newTableSlots.remove(timeFrom);
        table.getSlots().put(date.toString(), newTableSlots);
        tableService.updateTable(table);

        List<String> newWaiterSlots = new ArrayList<>(newWaiter.getSlots().getOrDefault(date.toString(),SlotUtil.getDefaultSlots()));
        newWaiterSlots.remove(timeFrom);
        newWaiter.getSlots().put(date.toString(), newWaiterSlots);
        newWaiter.setCount(newWaiter.getCount() + 1);
        waiterService.updateWaiter(newWaiter);

        // Update reservation details
        existingReservation.setLocationId(locationId);
        existingReservation.setTableNumber(tableNumber);
        existingReservation.setDate(date.toString());
        existingReservation.setGuestsNumber(guestsNumber.toString());
        existingReservation.setTimeSlot(timeFrom + " - " + timeTo);
        existingReservation.setWaiterId(newWaiter.getUserId());
        existingReservation.setWaiterName(newWaiter.getFirstName());
        existingReservation.setStatus(ReservationStatus.CONFIRMED);

        log.debug("Finalizing update for reservation ID: {}.", reservationId);
        reservationRepository.updateReservation(existingReservation);

        log.info("Reservation ID: {} successfully updated.", reservationId);

        // Return updated reservation response
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
