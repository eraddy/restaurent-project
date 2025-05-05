package com.epam.edai.run8.team11.utils;

import com.epam.edai.run8.team11.dto.SignUpDto;
import com.epam.edai.run8.team11.model.user.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MapperUtil {
    public User mapToUser(SignUpDto signUpDto){
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .email(signUpDto.getEmail())
                .firstName(signUpDto.getFirstName())
                .lastName(signUpDto.getLastName())
                .build();
    }
}
