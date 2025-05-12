package com.epam.edai.run8.team11.service.table;

import com.epam.edai.run8.team11.exception.location.LocationNotFoundException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.table.TableNotFoundException;
import com.epam.edai.run8.team11.model.table.Table;
import com.epam.edai.run8.team11.model.table.response.TableResponseDto;
import com.epam.edai.run8.team11.model.table.response.TableResponseDtoMapper;
import com.epam.edai.run8.team11.repository.table.TableRepository;
import com.epam.edai.run8.team11.service.location.LocationService;
import com.epam.edai.run8.team11.utils.SlotUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableServiceImpl implements TableService{

    private final TableRepository tableRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TableResponseDtoMapper tableResponseDtoMapper;

    @Override
    public List<TableResponseDto> getAvailableTables(String locationId, String date, int guestsNumber, String time) {
        log.debug("Fetching available tables for locationId: {}, date: {}, guestsNumber: {}, time: {}", locationId, date, guestsNumber, time);

        if(LocalDate.parse(date).isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Date can not be in past");
        }

        if(guestsNumber>4 || guestsNumber<=0) {
            throw new IllegalArgumentException("Guests number must be in a range of [1-4]");
        }

        if(!time.matches("^\\d{2}:\\d{2}$"))
           throw new IllegalArgumentException("Invalid Input time");

        List<Table> tables = tableRepository.findById(locationId);
        if (tables.isEmpty()) {
            log.error("No tables found for locationId: {}", locationId);
            throw new LocationNotFoundException("Either the given location id " + locationId + " is invalid or no table listed");
        }

        log.info("Total tables found for locationId {}: {}", locationId, tables.size());

        List<TableResponseDto> availableTables = tables.stream()
                .peek(table -> {
                    // Create a new instance of Table with slots containing only the relevant date
                    Map<String, List<String>> filteredSlots = Map.of(date, table.getSlots().getOrDefault(date, SlotUtil.getDefaultSlots()));
                    table.setSlots(filteredSlots);
                })
                .filter(table -> {
                    // Only include tables with the required time slot available
                    List<String> slotsForDate = table.getSlots().get(date);
                    return slotsForDate != null && slotsForDate.contains(time);
                })
                .map(tableResponseDtoMapper).toList();

        if (availableTables.isEmpty()) {
            log.error("No tables available for time: {} on date: {}", time, date);
            throw new SlotAlreadyBookedException("Slot not available");
        }

        log.info("Available tables for locationId {}: {}", locationId, availableTables.size());
        return availableTables;
    }

    @Override
    public boolean isTableAvailable(Table table, LocalDate date, String timeFrom) {
        log.debug("Checking availability for table {} at location {} on {} for time {}", table.getTableNumber(), table.getLocationId(), date, timeFrom);

        Map<String, List<String>> slots = table.getSlots();
        SlotUtil.populateSlotMapForDate(slots,date);
        log.debug("Slots for table {} on {}: {}", table.getTableNumber(), date, slots.get(date.toString()));
        return slots.get(date.toString()).contains(timeFrom);
    }

    @Override
    public void bookTableSlots(Table table, LocalDate date, String time) {
        // Retrieve the slots for the given date
        List<String> slots = table.getSlots().get(date.toString());
        if (slots != null) {
            // Make the list modifiable if it's immutable
            List<String> modifiableSlots = new ArrayList<>(slots);
            modifiableSlots.remove(time);

            // Update the slots map with the modified list
            table.getSlots().put(date.toString(), modifiableSlots);

        } else {
            log.warn("No table to book table");
        }
    }

    @Override
    public List<Table> findAll() {return tableRepository.findAll();
    }

    @Override
    public List<Table> findById(String locationID) {
        List<Table> locations = tableRepository.findById(locationID);
        if(locations.isEmpty())
            throw new LocationNotFoundException("There is no location with location id : "+locationID);

        return locations;
    }

    @Override
    public Table findByIdAndNumber(String locationId, Integer tableNumber) {
        List<Table> tables = findById(locationId);
        return tables.stream()
                .filter(table -> table.getTableNumber().equals(tableNumber))
                .findAny()
                .orElseThrow(() -> new TableNotFoundException(tableNumber));
    }

    @Override
    public void updateTable(Table table) {
        tableRepository.update(table);
    }
}
