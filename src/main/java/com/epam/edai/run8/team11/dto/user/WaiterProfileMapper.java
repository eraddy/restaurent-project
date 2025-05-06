package com.epam.edai.run8.team11.dto.user;

import com.epam.edai.run8.team11.model.user.Waiter;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class WaiterProfileMapper implements Function<Waiter, WaiterProfileDto> {
    @Override
    public WaiterProfileDto apply(Waiter waiter) {
        return WaiterProfileDto.builder()
                .firstName(waiter.getFirstName())
                .lastName(waiter.getLastName())
                .locationId(waiter.getLocationId())
                .imageUrl(waiter.getUserAvtarUrl())
                .build();
    }
}
