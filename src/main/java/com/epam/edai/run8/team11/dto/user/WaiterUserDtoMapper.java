package com.epam.edai.run8.team11.dto.user;

import com.epam.edai.run8.team11.model.user.Waiter;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class WaiterUserDtoMapper implements Function<Waiter, UserDto> {
    @Override
    public UserDto apply(Waiter waiter) {
        return UserDto.builder()
                .firstName(waiter.getFirstName())
                .lastName(waiter.getLastName())
                .email(waiter.getEmail())
                .role(waiter.getRole())
                .userId(waiter.getUserId())
                .build();
    }
}
