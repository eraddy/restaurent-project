package com.epam.edai.run8.team11.dto.user;

import com.epam.edai.run8.team11.model.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDetailsDtoMapper implements Function<User, UserDetailsDto> {
    @Override
    public UserDetailsDto apply(User user) {
        return UserDetailsDto.builder()
                .username(user.getFirstName() + " " + user.getLastName())
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }
}
