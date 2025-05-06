package com.epam.edai.run8.team11.service.booking;

import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByClient;
import com.epam.edai.run8.team11.dto.reservatation.ReservationResponse;
import com.epam.edai.run8.team11.exception.access.InvalidAccessException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.model.Table;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.user.role.Role;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.model.reservation.reservationstatus.ReservationStatus;
import com.epam.edai.run8.team11.service.TestUtils;
import com.epam.edai.run8.team11.service.location.LocationService;
import com.epam.edai.run8.team11.service.reservaiton.ReservationService;
import com.epam.edai.run8.team11.service.sqs.SqsService;
import com.epam.edai.run8.team11.service.table.TableService;
import com.epam.edai.run8.team11.service.waiter.WaiterService;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private WaiterService waiterService;

    @Mock
    private LocationService locationService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @Mock
    private TableService tableService;

    @Mock
    private SqsService sqsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void bookingByClient_ShouldThrowUserNotLoggedInException_WhenUserIsNotAuthenticated() {
        // Arrange
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.empty());
        ReservationRequestByClient request = new ReservationRequestByClient();

        // Act & Assert
        assertThrows(UserNotLoggedInException.class, () -> bookingService.bookingByClient(request));
    }

    @Test
    void bookingByClient_ShouldThrowInvalidAccessException_WhenUserIsNotCustomer() {
        // Arrange
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(TestUtils.createUserDto(Role.WAITER)));
        ReservationRequestByClient request = new ReservationRequestByClient();

        // Act & Assert
        assertThrows(InvalidAccessException.class, () -> bookingService.bookingByClient(request));
    }

    @Test
    void bookingByClient_ShouldThrowSlotAlreadyBookedException_WhenTableIsNotAvailable() {
        // Arrange
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(TestUtils.createUserDto(Role.CUSTOMER)));
        ReservationRequestByClient request = TestUtils.createReservationRequestByClient();
        Table table = TestUtils.createTable();

        when(tableService.findByIdAndNumber(request.getLocationId(), Integer.valueOf(request.getTableNumber()))).thenReturn(table);
        when(tableService.isTableAvailable(table, LocalDate.parse(request.getDate()), request.getTimeFrom())).thenReturn(false);

        // Act & Assert
        assertThrows(SlotAlreadyBookedException.class, () -> bookingService.bookingByClient(request));
    }

    @Test
    void bookingByClient_ShouldReturnReservationResponse_WhenBookingIsSuccessful() {
        // Arrange
        when(authenticationUtil.getAuthenticatedUser()).thenReturn(Optional.of(TestUtils.createUserDto(Role.CUSTOMER)));
        ReservationRequestByClient request = TestUtils.createReservationRequestByClient();
        Table table = TestUtils.createTable();
        Waiter waiter = TestUtils.createWaiter();

        when(tableService.findByIdAndNumber(request.getLocationId(), Integer.valueOf(request.getTableNumber()))).thenReturn(table);
        when(tableService.isTableAvailable(table, LocalDate.parse(request.getDate()), request.getTimeFrom())).thenReturn(true);
        when(waiterService.getLeastBusyWaiterAtLocation(request.getLocationId(), LocalDate.parse(request.getDate()), request.getTimeFrom())).thenReturn(waiter);
        when(reservationService.makeReservation(any(Reservation.class))).thenReturn(true);

        // Act
        ReservationResponse response = bookingService.bookingByClient(request);

        // Assert
        assertNotNull(response);
        assertEquals(ReservationStatus.CONFIRMED, response.getStatus());
        verify(reservationService, times(1)).makeReservation(any(Reservation.class));
    }
}
