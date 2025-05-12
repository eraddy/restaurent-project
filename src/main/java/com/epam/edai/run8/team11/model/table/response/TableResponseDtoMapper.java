package com.epam.edai.run8.team11.model.table.response;

import com.epam.edai.run8.team11.model.table.Table;
import com.epam.edai.run8.team11.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TableResponseDtoMapper implements Function<Table, TableResponseDto> {

    @Autowired
    @Lazy
    private LocationService locationService;

    @Override
    public TableResponseDto apply(Table table) {
        String locationAddress = locationService != null ?
                locationService.findLocationById(table.getLocationId()).getAddress() : "";

        return TableResponseDto.builder()
                .locationId(table.getLocationId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .slots(table.getSlots())
                .locationAddress(locationAddress)
                .build();
    }
}
