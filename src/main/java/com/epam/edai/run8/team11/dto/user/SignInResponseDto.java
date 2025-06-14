package com.epam.edai.run8.team11.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInResponseDto {
    private String accessToken;
    private String username;
    private String role;
}
