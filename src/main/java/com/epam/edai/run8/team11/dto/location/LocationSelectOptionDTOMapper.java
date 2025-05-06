package com.epam.edai.run8.team11.dto.location;

import com.epam.edai.run8.team11.model.Location;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LocationSelectOptionDTOMapper implements Function<Location, LocationSelectOptionDTO> {
    @Override
    public LocationSelectOptionDTO apply(Location location) {
        return LocationSelectOptionDTO.builder()
                .id(location.getLocationId())
                .address(location.getName())
                .build();
    }
}
