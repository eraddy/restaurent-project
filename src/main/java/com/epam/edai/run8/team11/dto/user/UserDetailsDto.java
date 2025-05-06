package com.epam.edai.run8.team11.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsDto {
    private String username;
    private String userId;
    private String email;
}
