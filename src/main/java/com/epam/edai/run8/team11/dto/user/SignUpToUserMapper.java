package com.epam.edai.run8.team11.dto.user;

import com.epam.edai.run8.team11.dto.SignUpDto;
import com.epam.edai.run8.team11.model.user.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SignUpToUserMapper implements Function<SignUpDto, User> {
    @Override
    public User apply(SignUpDto signUpDto) {
        return User.builder()
                .lastName(signUpDto.getLastName())
                .firstName(signUpDto.getFirstName())
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .build();
    }
}
