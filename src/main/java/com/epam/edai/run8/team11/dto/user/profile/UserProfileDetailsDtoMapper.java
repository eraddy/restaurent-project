package com.epam.edai.run8.team11.dto.user.profile;

import com.epam.edai.run8.team11.model.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserProfileDetailsDtoMapper implements Function<User, UserProfileDetailsDto> {
    @Override
    public UserProfileDetailsDto apply(User user) {
        return UserProfileDetailsDto.builder()
                .username(user.getFirstName() + " " + user.getLastName())
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }
}
