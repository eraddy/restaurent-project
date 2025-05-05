package com.epam.edai.run8.team11.dto.reservatation;

import com.epam.edai.run8.team11.exception.InvalidInputException;
import com.epam.edai.run8.team11.exception.location.LocationException;
import com.epam.edai.run8.team11.exception.table.TableNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestByClient {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String locationId;
    private String tableNumber;
    private String date;
    private Integer guestsNumber;
    private String timeFrom;
    private String timeTo;

    public void validate() {
        if (locationId == null || locationId.isBlank()) {
            throw new LocationException("Location ID must not be null or empty.");
        }
        if (tableNumber == null || tableNumber.isBlank()) {
            throw new IllegalArgumentException("Table number must not be null or empty.");
        }
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date must not be null or empty.");
        }
        // Validate date format and ensure it is not in the past
        LocalDate parsedDate = LocalDate.parse(date,DATE_FORMATTER); // Throws DateTimeParseException if invalid
        if (parsedDate.isBefore(LocalDate.now())) {
            throw new DateTimeException("Date must not be in the past.");
        }
        if(parsedDate.isAfter(LocalDate.now().plusMonths(1)))
        {
            throw new InvalidInputException("Can't make reservation one month ahead");
        }
        if (guestsNumber == null) {
            throw new IllegalArgumentException("Number of guests must not be null or empty.");
        }
        if (guestsNumber <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0.");
        }
        if (guestsNumber > 4) { // Adjust max as per business logic
            throw new IllegalArgumentException("Number of guests cannot exceed the maximum limit of 4.");
        }

        if (timeFrom == null || timeFrom.isBlank()) {
            throw new IllegalArgumentException("Start time must not be null or empty.");
        }
        LocalTime parsedStartTime = LocalTime.parse(timeFrom); // Throws DateTimeParseException if invalid

        if (timeTo == null || timeTo.isBlank()) {
            throw new IllegalArgumentException("End time must not be null or empty.");
        }
        LocalTime parsedEndTime = LocalTime.parse(timeTo); // Throws DateTimeParseException if invalid
        if (!parsedEndTime.isAfter(parsedStartTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }
}