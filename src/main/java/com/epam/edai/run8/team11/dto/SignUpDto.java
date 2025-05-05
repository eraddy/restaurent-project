package com.epam.edai.run8.team11.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignUpDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}

