package com.epam.edai.run8.team11.dto.user.waiter.profile;

import com.epam.edai.run8.team11.model.user.Waiter;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class WaiterProfileDetailsDtoMapper implements Function<Waiter, WaiterProfileDetailsDto> {
    @Override
    public WaiterProfileDetailsDto apply(Waiter waiter) {
        return WaiterProfileDetailsDto.builder()
                .firstName(waiter.getFirstName())
                .lastName(waiter.getLastName())
                .locationId(waiter.getLocationId())
                .imageUrl(waiter.getUserAvtarUrl())
                .build();
    }
}
