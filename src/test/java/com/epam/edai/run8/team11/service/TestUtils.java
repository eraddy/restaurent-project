package com.epam.edai.run8.team11.service;

import com.epam.edai.run8.team11.dto.reservatation.ReservationRequestByClient;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.model.Table;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.user.role.Role;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class TestUtils {

    public static UserDto createUserDto(Role role) {
        return UserDto.builder()
                .userId("user123")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(role)
                .build();
    }

    public static ReservationRequestByClient createReservationRequestByClient() {
        return new ReservationRequestByClient(
                "location123",
                "1",
                LocalDate.now().toString(),
                2,
                "10:30",
                "12:00"
        );
    }

    public static Table createTable() {
        Map<String, List<String>> slots = new HashMap<>();
        slots.put("2023-10-10", List.of("10:30", "12:15", "14:00"));

        return Table.builder()
                .locationId("location123")
                .tableNumber(1)
                .capacity(4)
                .slots(slots)
                .build();
    }

    public static Waiter createWaiter() {
        Map<String, List<String>> slots = new HashMap<>();
        slots.put("2023-10-10", List.of("10:30", "12:15", "14:00"));

        return new Waiter(
                slots,
                "location123",
                0
        );
    }
}
