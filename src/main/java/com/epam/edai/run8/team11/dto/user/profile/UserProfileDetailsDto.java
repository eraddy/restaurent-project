package com.epam.edai.run8.team11.dto.user.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDetailsDto {
    private String username;
    private String userId;
    private String email;
}
