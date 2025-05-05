package com.epam.edai.run8.team11.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInDto {
    private String email;
    private String password;
}

